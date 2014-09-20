package com.googlecode.lanterna.gui2;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.input.KeyStroke;

/**
 * Default implementation of Interactable that extends from AbstractComponent. You will want to extend from this class
 * if you want to make your own interactable component.
 * @author Martin
 */
public abstract class AbstractInteractableComponent extends AbstractComponent implements Interactable {

    private boolean inFocus;

    protected AbstractInteractableComponent() {
        inFocus = false;
    }

    @Override
    public TerminalPosition getCursorLocation() {
        return TerminalPosition.TOP_LEFT_CORNER;
    }

    @Override
    public final void onEnterFocus(FocusChangeDirection direction, Interactable previouslyInFocus) {
        inFocus = true;
        afterEnterFocus(direction, previouslyInFocus);
    }

    protected void afterEnterFocus(FocusChangeDirection direction, Interactable previouslyInFocus) {
    }

    @Override
    public final void onLeaveFocus(FocusChangeDirection direction, Interactable nextInFocus) {
        inFocus = false;
        afterLeaveFocus(direction, nextInFocus);
    }

    protected void afterLeaveFocus(FocusChangeDirection direction, Interactable nextInFocus) {
    }

    @Override
    public boolean isFocused() {
        return inFocus;
    }

    @Override
    public Result handleKeyStroke(KeyStroke keyStroke) {
        switch (keyStroke.getKeyType()) {
            case ArrowDown:
                return Result.MOVE_FOCUS_DOWN;
            case ArrowLeft:
                return Result.MOVE_FOCUS_LEFT;
            case ArrowRight:
                return Result.MOVE_FOCUS_RIGHT;
            case ArrowUp:
                return Result.MOVE_FOCUS_UP;
            case Tab:
                return Result.MOVE_FOCUS_NEXT;
            case ReverseTab:
                return Result.MOVE_FOCUS_PREVIOUS;
        }
        return Result.UNHANDLED;
    }

    @Override
    protected void setThemeRenderer(ComponentRenderer<? extends Component> renderer) {
        if(!(renderer instanceof InteractableRenderer)) {
            throw new IllegalArgumentException("Cannot assign " + renderer + " as renderer for " + toString() + ", " +
                    "need to implement InteractableRenderer");
        }
        super.setThemeRenderer(renderer);
    }

    @Override
    protected InteractableRenderer<? extends Interactable> getThemeRenderer() {
        return (InteractableRenderer<? extends Interactable>)super.getThemeRenderer();
    }
}