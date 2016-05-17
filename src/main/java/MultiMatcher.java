import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MultiMatcher implements ReplacerDFA.Matcher {

    static class TreeNode {
        public final Map<Character, TreeNode> children = new HashMap<>();
        public boolean isCompleteMatch = false;
    }

    private final TreeNode rootNode;

    private TreeNode currentNode;
    private StringBuilder stringMatched;


    public MultiMatcher(List<char[]> stringsToMatch) {
        rootNode = buildSearchTree(stringsToMatch);
        reset();
    }

    private TreeNode buildSearchTree(List<char[]> stringsToMatch) {
        TreeNode root = new TreeNode();
        for (char[] string: stringsToMatch) {
            appendChildren(root, string, 0);
        }
        return root;
    }

    private void appendChildren(TreeNode node, char[] string, int index) {
        boolean complete = index == string.length;
        if (complete) {
            node.isCompleteMatch = true;
        } else {
            char currentChar = string[index];
            if (!node.children.containsKey(currentChar)) {
                node.children.put(currentChar, new TreeNode());
            }
            final TreeNode nextNode = node.children.get(currentChar);
            appendChildren(nextNode, string, index + 1);
        }
    }

    @Override
    public Result process(char c) {

        final boolean matched = currentNode.children.containsKey(c);
        if (matched) {
            currentNode = currentNode.children.get(c);
            stringMatched.append(c);
        } else {
            reset();
        }

        final boolean complete = currentNode.isCompleteMatch;

        final Optional<String> matchedString = complete
                ? Optional.of(stringMatched.toString())
                : Optional.empty();

        if (complete) {
            reset();
        }

        return new Result(matched, complete, matchedString);
    }

    @Override
    public void reset() {
        stringMatched = new StringBuilder();
        currentNode = rootNode;
    }

}
