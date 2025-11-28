package com.example.sortify.util;

import com.example.sortify.model.UserAction;
import java.util.ArrayDeque;
import java.util.Deque;

public class UndoManager {
    private final Deque<UserAction> undo = new ArrayDeque<>();
    private final Deque<UserAction> redo = new ArrayDeque<>();

    public void execute(UserAction a){
        a.doIt();
        undo.push(a);
        redo.clear();
    }

    public boolean canUndo(){ return !undo.isEmpty(); }
    public boolean canRedo(){ return !redo.isEmpty(); }

    public void undo(){
        if (canUndo()){
            UserAction a = undo.pop();
            a.undo();
            redo.push(a);
        }
    }

    public void redo(){
        if (canRedo()){
            UserAction a = redo.pop();
            a.doIt();
            undo.push(a);
        }
    }
}

