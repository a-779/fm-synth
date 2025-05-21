package javasynth;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * MIDI keyboard listener that connects to a user-selected MIDI input device
 * and routes note events to the Synth.
 */
public class Keyboard implements Receiver {

    private MidiDevice connectedDevice;

    public Keyboard() throws MidiUnavailableException {
        MidiDevice.Info[] inputDevices = getInputDevices();
        int selectedIndex = promptUserToChooseDevice(inputDevices);
        connectToDevice(inputDevices[selectedIndex]);
    }

    // Return only input-capable devices
    private MidiDevice.Info[] getInputDevices() {
        List<MidiDevice.Info> inputs = new ArrayList<>();
        for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
            try {
                MidiDevice device = MidiSystem.getMidiDevice(info);
                if (device.getMaxTransmitters() != 0) {
                    inputs.add(info);
                }
            } catch (MidiUnavailableException e) {
                // Ignore bad devices
            }
        }
        return inputs.toArray(new MidiDevice.Info[0]);
    }

    // Prompt user to select a MIDI device
    private int promptUserToChooseDevice(MidiDevice.Info[] infos) {
        System.out.println("Available MIDI Input Devices:");
        for (int i = 0; i < infos.length; i++) {
            System.out.printf(" [%d] %s%n", i, infos[i].getName());
        }

        Scanner scanner = new Scanner(System.in);
        int index = -1;
        while (index < 0 || index >= infos.length) {
            System.out.print("Select MIDI device by number: ");
            if (scanner.hasNextInt()) {
                index = scanner.nextInt();
            } else {
                scanner.next(); // discard invalid input
            }
        }
        return index;
    }

    // Connect to the selected MIDI device
    private void connectToDevice(MidiDevice.Info info) throws MidiUnavailableException {
        MidiDevice device = MidiSystem.getMidiDevice(info);
        device.open();
        Transmitter transmitter = device.getTransmitter();
        transmitter.setReceiver(this);
        this.connectedDevice = device;
        System.out.println("Connected to: " + info.getName());
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        if (message instanceof ShortMessage sm) {
            int command = sm.getCommand();
            int key = sm.getData1();
            int velocity = sm.getData2();

            switch (command) {
                case ShortMessage.NOTE_ON -> {
                    if (velocity > 0) {
                        Synth.play(midiNoteToFreq(key));
                    } else {
                        Synth.noteOff(midiNoteToFreq(key));
                    }
                }
                case ShortMessage.NOTE_OFF -> Synth.noteOff(midiNoteToFreq(key));
            }
        }
    }

    @Override
    public void close() {
        if (connectedDevice != null && connectedDevice.isOpen()) {
            connectedDevice.close();
        }
    }

    private int midiNoteToFreq(int midiNote) {
        return (int) (440 * Math.pow(2, (midiNote - 69) / 12.0));
    }
}
