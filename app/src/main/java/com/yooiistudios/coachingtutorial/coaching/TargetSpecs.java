package com.yooiistudios.coachingtutorial.coaching;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Dongheyon Jeong in CoachingTutorial from Yooii Studios Co., LTD. on 15. 7. 17.
 *
 * TargetSpecs
 *  TargetSpec 들의 리스트를 래핑
 */
public class TargetSpecs implements Iterable<TargetSpec> {
//    private ArrayList<TargetSpec> mTargetSpecs = new ArrayList<>();
    private Queue<TargetSpec> mTargetSpecs = new LinkedList<>();

    public TargetSpecs() {

    }

    public void add(TargetSpec targetSpec) {
        mTargetSpecs.add(targetSpec);
    }

    public int size() {
        return mTargetSpecs.size();
    }

    public TargetSpec next() {
        return mTargetSpecs.poll();
    }

    @Override
    public Iterator<TargetSpec> iterator() {
        return mTargetSpecs.iterator();
    }

    public boolean hasNext() {
        return mTargetSpecs.size() > 0;
    }
}
