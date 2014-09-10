package me.geso.tinyvalidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class Node {
    private final Node parent;
    private final String name;

    Node() {
        this.parent = null;
        this.name = null;
    }

    Node(final Node parent, final String name) {
        this.parent = parent;
        this.name = name;
    }

    Node child(final String name) {
        return new Node(this, name);
    }

    List<String> toList() {
        Node n = this;
        List<String> path = new ArrayList< >();
        while (n.parent != null) {
            path.add(n.name);
            n = n.parent;
        }
        Collections.reverse(path);
        return path;
    }

    public String toString() {
        return this.toList().stream().collect(Collectors.joining("."));
    }
}
