package com.java.exercises;

import javax.sound.midi.*;
import javax.swing.*;


class BeatBoxMidi  {
    private Sequencer beatBoxSequencer;
    private Sequence beat;
    private Track loop;
    private int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};
    private static final float TEMPO_UP_MULTIPLIER = 1.03f;
    private static final float TEMPO_DOWN_MULTIPLIER = 0.97f;

    BeatBoxMidi() throws InvalidMidiDataException,MidiUnavailableException {

        beatBoxSequencer = MidiSystem.getSequencer();
        beatBoxSequencer.open();
        beat = new Sequence(Sequence.PPQ, 4);
        loop = beat.createTrack();
        beatBoxSequencer.setTempoInBPM(120);
    }

    void addCheckBoxToTrack(int chanel, int j, int velocity, int checkNumber) {
        int time = (checkNumber % 16) * 5 + 1;
        int note = (j / 16);
        loop.add(makeEvent(144, chanel, instruments[note], velocity, time));
        loop.add(makeEvent(128, chanel, instruments[note], velocity, time + 1));
    }


    private MidiEvent makeEvent(int onOrOff, int chanel, int note, int velocity, int time) {
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(onOrOff, chanel, note, velocity);
            event = new MidiEvent(a, time);
            System.out.println("in enterNotes " + onOrOff + " " + chanel + " " + note + " " + velocity + " " + time);
        } catch (InvalidMidiDataException ex) {
            JOptionPane.showMessageDialog(null, "Oops something went wrong!\n Please restart the program.");
        }
        return event;
    }


    void startPlaying() {
        try {
            loop.add(makeEvent(172, 1, 127, 0, 80));
            loop.add(makeEvent(192, 9, 1, 0, 76));
            beatBoxSequencer.setSequence(beat);
            beatBoxSequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            Thread.sleep(3000); // If not delayed it starts playing immediately thus resulting sometimes in distortion of the beat
            beatBoxSequencer.start();

        } catch (InterruptedException | InvalidMidiDataException ex) {
            JOptionPane.showMessageDialog(null, "Oops something went wrong!\n Please restart the program.");
        }
    }

    void stopPlaying() {
        beatBoxSequencer.stop();
    }

    void setTempoUp() {
        float tempo = beatBoxSequencer.getTempoFactor();
        beatBoxSequencer.setTempoFactor(tempo * TEMPO_UP_MULTIPLIER);
    }

    void setTempoDown() {
        float tempo = beatBoxSequencer.getTempoFactor();
        beatBoxSequencer.setTempoFactor(tempo * TEMPO_DOWN_MULTIPLIER);
    }
}
