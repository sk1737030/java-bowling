package bowling.domain.frame;

import bowling.domain.score.Score;
import bowling.domain.score.Scores;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class Frames implements Iterable<Frame> {

    private final List<Frame> frames;

    public Frames(List<Frame> frames) {
        this.frames = frames;
    }

    public static Frames create() {
        return new Frames(new ArrayList<>(Collections.singletonList(NormalFrame.create())));
    }

    public static Frames from(Frame frame) {
        return new Frames(new ArrayList<>(Collections.singletonList(frame)));
    }

    public void addFrame(Frame frame) {
        frames.add(frame);
    }

    public int size() {
        return frames.size();
    }

    public Frame latestFrame() {
        return frames.get(frames.size() - 1);
    }

    public Frame get(int frame) {
        return frames.get(frame);
    }

    @Override
    public Iterator<Frame> iterator() {
        return frames.iterator();
    }

    @Override
    public void forEach(Consumer<? super Frame> action) {
        frames.forEach(action);
    }

    public boolean hasNextRoundScore(Frame currentFrame) {
        return latestFrame().compareTo(currentFrame) > 0;
    }

    public boolean hasNextNextRoundScore(Frame frame) {
        if (hasNextRoundScore(frame)) {
            Frame nextFrame = nextFrame(frame);
            return hasNextRoundScore(nextFrame) && nextFrame(nextFrame).isRolled();
        }

        return false;
    }

    public Frame nextFrame(Frame normalFrame) {
        for (int i = 0; i < frames.size(); i++) {
            if (frames.get(i).equals(normalFrame)) {
                return frames.get(i + 1);
            }
        }

        throw new IllegalArgumentException("주어진 프레임 다음 프레임을 찾을 수 없습니다.");
    }

    public Frame prev(Frame normalFrame) {
        for (int i = 0; i < frames.size(); i++) {
            if (frames.get(i).equals(normalFrame)) {
                return frames.get(i - 1);
            }
        }

        throw new IllegalArgumentException("주어진 프레임을 찾을 수 없습니다.");
    }

    public Scores scores() {
        Scores scores = Scores.create();
        scores.scores(this);
        return scores;
    }

    public Score calcStrike(NormalFrame strikeFrame) {
        Score score = Score.create();
        if (!hasNextNextRoundScore(strikeFrame)) {
            return score;
        }

        Frame nextFrame = nextFrame(strikeFrame);
        return score.sumWithFrames(strikeFrame, nextFrame, nextFrame(nextFrame));
    }

    public Score calcSpare(NormalFrame spareFrame) {
        Score score = Score.create();

        if (!hasNextRoundScore(spareFrame)) {
            return score;
        }

        return score.sumWithFrames(spareFrame, prev(spareFrame), nextFrame(spareFrame));
    }

    public Score calcMiss(NormalFrame missFrame) {
        Score score = Score.create();
        return score.sumWithFrames(missFrame, prev(missFrame));
    }
}
