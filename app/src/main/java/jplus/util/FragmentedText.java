package jplus.util;

import jplus.generator.TextChangeRange;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FragmentedText {
    private final String original;
    private final TextChangeRange originalTextChangeRange;
    private final Map<Integer, List<TextFragmentNode>> lineFragmentMap;

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
        this.lineFragmentMap = new TreeMap<>();

        String[] lines = original.split("\n", -1);
        int currentGlobalIndex = 0;

        int lineNumber = range.startLine();
        for (int i = 0; i < lines.length; i++) {
            String lineStr = lines[i];

            int startIdx, endIdx;

            if (range.startLine() == range.endLine()) {
                startIdx = range.startIndex();
                endIdx = range.inclusiveEndIndex();
            } else {
                if (lineNumber == range.startLine()) {
                    startIdx = range.startIndex();
                    endIdx = range.startIndex() + lineStr.length() - 1;
                } else if (lineNumber == range.endLine()) {
                    startIdx = 0;
                    endIdx = range.inclusiveEndIndex();
                } else {
                    startIdx = 0;
                    endIdx = lineStr.length() - 1;
                }
            }

            TextChangeRange lineRange = new TextChangeRange(lineNumber, startIdx, lineNumber, endIdx);
            List<TextFragmentNode> fragments = new LinkedList<>();
            fragments.add(new TextFragmentNode(lineRange, lineStr, false));
            lineFragmentMap.put(lineNumber, fragments);
            ++lineNumber;
        }
    }

    public void update(TextChangeRange textChangeRange, String replace) {
        int startLine = textChangeRange.startLine();
        int endLine = textChangeRange.endLine();

        String[] replacementLines = replace.split("\n", -1);
        List<TextFragmentNode> targetNodes = new ArrayList<>();

        for (int line = startLine; line <= endLine; line++) {
            List<TextFragmentNode> fragments = lineFragmentMap.get(line);
            if (fragments == null) continue;

            List<TextFragmentNode> newFragments = new LinkedList<>();
            Iterator<TextFragmentNode> iterator = fragments.iterator();

            while (iterator.hasNext()) {
                TextFragmentNode node = iterator.next();
                TextChangeRange nodeRange = node.originalRange;

                if (!textChangeRange.overlaps(nodeRange)) {
                    newFragments.add(node);
                    continue;
                }

                if (textChangeRange.equals(nodeRange)) {
                    newFragments.add(node);
                    targetNodes.add(node);
                    continue;
                }

                if (node.rangeFixed) {
                    throw new IllegalArgumentException("Cannot partially overwrite a fixed range: " + nodeRange);
                }

                int nodeLine = nodeRange.startLine();
                int nodeStart = nodeRange.startIndex();
                int nodeEnd = nodeRange.inclusiveEndIndex();

                int rangeStart;
                int rangeEnd;
                if (textChangeRange.startLine() == textChangeRange.endLine()) {
                    rangeStart = textChangeRange.startIndex();
                    rangeEnd = textChangeRange.inclusiveEndIndex();
                } else {
                    if (line == textChangeRange.startLine()) {
                        rangeStart = textChangeRange.startIndex();
                        rangeEnd = Integer.MAX_VALUE;
                    } else if (line == textChangeRange.endLine()) {
                        rangeStart = 0;
                        rangeEnd = textChangeRange.inclusiveEndIndex();
                    } else {
                        rangeStart = 0;
                        rangeEnd = Integer.MAX_VALUE;
                    }
                }

                int overlapStart = Math.max(rangeStart, nodeStart);
                int overlapEnd = Math.min(rangeEnd, nodeEnd);

                int relStart = overlapStart - nodeStart;
                int relEnd = overlapEnd - nodeStart + 1;

                if (overlapStart > nodeStart) {
                    TextChangeRange headRange = new TextChangeRange(nodeLine, nodeStart, nodeLine, overlapStart - 1);
                    String head = node.string.substring(0, relStart);
                    newFragments.add(new TextFragmentNode(headRange, head, false));
                }

                node.originalRange = new TextChangeRange(nodeLine, overlapStart, nodeLine, overlapEnd);;
                newFragments.add(node);
                targetNodes.add(node);

                if (overlapEnd < nodeEnd) {
                    TextChangeRange tailRange = new TextChangeRange(nodeLine, overlapEnd + 1, nodeLine, nodeEnd);
                    String tail = node.string.substring(relEnd);
                    newFragments.add(new TextFragmentNode(tailRange, tail, false));
                }
            }

            if (newFragments.isEmpty()) {
                lineFragmentMap.remove(line);
            } else {
                lineFragmentMap.put(line, newFragments);
            }
        }

    //    if (targetNodes.isEmpty()) {
    //        throw new IllegalStateException("No nodes were affected by the given range.");
    //    }

        for (int i = 0; i < replacementLines.length; i++) {
            String lineContent = replacementLines[i];
            int targetLine = startLine + i;

            TextChangeRange newRange;
            if (i < targetNodes.size()) {
                TextFragmentNode deletedNode = targetNodes.get(i);
                deletedNode.string = lineContent;
            } else {
                TextFragmentNode last = targetNodes.get(targetNodes.size() - 1);
    //            int lineOffset = i - targetNodes.size() + 1;
    //            int newLine = last.originalRange.startLine() + lineOffset;
    //            newRange = new TextChangeRange(newLine, 0, newLine, lineContent.length() - 1);
                newRange = new TextChangeRange(targetLine, 0, targetLine, lineContent.length() - 1);
                TextFragmentNode newNode = new TextFragmentNode(newRange, lineContent, true);
                lineFragmentMap.computeIfAbsent(newRange.startLine(), k -> new LinkedList<>()).add(newNode);
            }


        }
    }

    public String debugString() {
        StringBuilder sb = new StringBuilder();
        lineFragmentMap.forEach((lineNumber, textFragmentNodeList) -> {
            sb.append("line:").append(lineNumber).append("\n");
            for (TextFragmentNode node : textFragmentNodeList) {
                sb.append("[")
                .append(node.originalRange.startIndex())
                .append("-")
                .append(node.originalRange.inclusiveEndIndex())
                .append(node.rangeFixed ? " FIXED" : "")
                .append("]: ")
                .append(node.string)
                .append("\n");
            }
        });
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        lineFragmentMap.forEach((line, textFragmentNodeList) -> {
            textFragmentNodeList.forEach(textFragmentNode -> {
                sb.append(textFragmentNode.string);
            });
            sb.append("\n");
        });
        if (sb.charAt(sb.length()-1) == '\n') sb.setLength(sb.length()-1);
        return sb.toString();
    }
}
