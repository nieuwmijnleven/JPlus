package jplus.util;

import jplus.generator.TextChangeRange;

import java.util.LinkedList;
import java.util.List;

public class FragmentedText {
    private final String original;
    private final TextChangeRange originalTextChangeRange;
    private final List<TextFragmentNode> fragmentedNodeList;

    static class TextFragmentNode {
        TextChangeRange originalRange;
        String string;
        boolean rangeFixed;

        public TextFragmentNode(TextChangeRange originalRange, String string, boolean rangeFixed) {
            this.originalRange = originalRange;
            this.string = string;
            this.rangeFixed = rangeFixed;
        }

        @Override
        public String toString() {
            return "TextFragmentNode{" +
                    "originalRange=" + originalRange +
                    ", string='" + string + '\'' +
                    ", rangeFixed=" + rangeFixed +
                    '}';
        }
    }

    public FragmentedText(TextChangeRange range, String original) {
        this.original = original;
        this.originalTextChangeRange = range;
        this.fragmentedNodeList = new LinkedList<>();
        this.fragmentedNodeList.add(new TextFragmentNode(range, original, false));
    }

    public FragmentedText(String original) {
        this(Utils.computeTextChangeRange(original, 0, original.length()-1), original);
    }

    public void update(TextChangeRange textChangeRange, String replace) {
        int startLine = textChangeRange.startLine();
        int endLine = textChangeRange.endLine();

        int affectedRangeCount = 0;
        for (int i = 0; i < fragmentedNodeList.size(); ++i) {
            TextFragmentNode node = fragmentedNodeList.get(i);
            TextChangeRange nodeRange = node.originalRange;

            if (!textChangeRange.overlaps(nodeRange)) {
                continue;
            }

            if (textChangeRange.equals(nodeRange)) {
                node.string = replace;
                ++affectedRangeCount;
                break;
            }

            if (node.rangeFixed) {
                throw new IllegalArgumentException("Cannot partially overwrite a fixed range: " + nodeRange);
            }

            int nodeStart = Utils.getIndexFromLineColumn(this.original, this.originalTextChangeRange, nodeRange.startLine(), nodeRange.startIndex());
            int nodeEnd = Utils.getIndexFromLineColumn(this.original, this.originalTextChangeRange, nodeRange.endLine(), nodeRange.inclusiveEndIndex());

            int rangeStart = Utils.getIndexFromLineColumn(this.original, this.originalTextChangeRange, textChangeRange.startLine(), textChangeRange.startIndex());
            int rangeEnd = Utils.getIndexFromLineColumn(this.original, this.originalTextChangeRange, textChangeRange.endLine(), textChangeRange.inclusiveEndIndex());

            int overlapStart = Math.max(rangeStart, nodeStart);
            int overlapEnd = Math.min(rangeEnd, nodeEnd);

            int relStart = overlapStart - nodeStart;
            int relEnd = overlapEnd - nodeStart + 1;

            if (overlapStart > nodeStart) {
                TextChangeRange headRange = Utils.getRangeFromStartIndexAndEndIndex(this.original, this.originalTextChangeRange, nodeStart, overlapStart - 1);
                String head = node.string.substring(0, relStart);
                fragmentedNodeList.add(i, new TextFragmentNode(headRange, head, false));
                ++i;
            }

            if (overlapEnd < nodeEnd) {
                TextChangeRange tailRange = Utils.getRangeFromStartIndexAndEndIndex(this.original, this.originalTextChangeRange, overlapEnd + 1, nodeEnd);
                String tail = node.string.substring(relEnd);
                fragmentedNodeList.add(i + 1, new TextFragmentNode(tailRange, tail, false));
                ++i;
            }

            node.originalRange = Utils.getRangeFromStartIndexAndEndIndex(this.original, this.originalTextChangeRange, overlapStart, overlapEnd);
            node.string = replace;
            node.rangeFixed = true;

            ++affectedRangeCount;
        }

        if (affectedRangeCount == 0) {
            throw new IllegalStateException(textChangeRange + " " + replace + " " + "No nodes were affected by the given range." + toString());
        }
    }

    public String debugString() {
        StringBuilder sb = new StringBuilder();
        for (TextFragmentNode node : fragmentedNodeList) {
            sb.append("[")
            .append(node.originalRange)
            .append(node.rangeFixed ? " FIXED" : "")
            .append("]:\n")
            .append(node.string)
            .append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        if (fragmentedNodeList.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        fragmentedNodeList.forEach(textFragmentNode -> {
            sb.append(textFragmentNode.string);
        });
        return sb.toString();
    }
}
