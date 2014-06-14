/*
 * This file is part of lanterna (http://code.google.com/p/lanterna/).
 * 
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) 2010-2014 Martin
 */
package com.googlecode.lanterna.screen;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.TerminalPosition;
import com.googlecode.lanterna.terminal.TerminalSize;
import java.io.IOException;

/**
 * Screen is a fundamental layer in Lanterna, presenting the terminal as a bitmap-like surface where you can perform
 * smaller in-memory operations to a back-buffer, effectively painting out the terminal as you'd like it, and then call
 * {@code refresh} to have the screen automatically apply the changes in the back-buffer to the real terminal. The 
 * screen tracks what's visible through a front-buffer, but this is completely managed internally and cannot be expected
 * to know what the terminal looks like if it's being modified externally.
 * </p>
 * If you want to do more complicated drawing operations, please see the class {@code ScreenWriter} which has many
 * utility methods that works on Screens.
 *
 * @author Martin
 */
public interface Screen {
    /**
     * Before you can use a Screen, you need to start it. By starting the screen, Lanterna will make sure the terminal
     * is in private mode (Screen only supports private mode), clears it (so that is can set the front and back buffers
     * to a known value) and places the cursor in the top left corner. After calling startScreen(), you can begin using
     * the other methods on this interface. When you want to exit from the screen and return to what you had before,
     * you can call {@code stopScreen()}.
     *
     * @throws IOException if there was an underlying IO error when exiting from private mode
     */
    void startScreen() throws IOException;

    /**
     * Calling this method will make the underlying terminal leave private mode, effectively going back to whatever
     * state the terminal was in before calling {@code startScreen()}. Once a screen has been stopped, you can start it
     * again with {@code startScreen()} which will restore the screens content to the terminal.
     *
     * @throws IOException if there was an underlying IO error when exiting from private mode
     */
    void stopScreen() throws IOException;

    /**
     * Erases all the characters on the screen, effectively giving you a blank area. The default background color will
     * be used. This is effectively the same as calling 
     * "fill(TerminalPosition.TOP_LEFT_CORNER, getSize(), TextColor.ANSI.Default)".
     * <p/>
     * Please note that calling this method will only affect the back buffer, you need to call refresh to make the 
     * change visible.
     */
    void clear();

    /**
     * A screen implementation typically keeps a location on the screen where the cursor will be placed after drawing
     * and refreshing the buffers, this method returns that location. If it returns null, it means that the terminal 
     * will attempt to hide the cursor (if supported by the terminal).
     * 
     * @return Position where the cursor will be located after the screen has been refreshed or {@code null} if the
     * cursor is not visible
     */
    TerminalPosition getCursorPosition();
    
    /**
     * A screen implementation typically keeps a location on the screen where the cursor will be placed after drawing
     * and refreshing the buffers, this method controls that location. If you pass null, it means that the terminal 
     * will attempt to hide the cursor (if supported by the terminal).
     *
     * @param position TerminalPosition of the new position where the cursor should be placed after refresh(), or if 
     * {@code null}, hides the cursor
     */
    void setCursorPosition(TerminalPosition position);

    /**
     * Gets the behaviour for what to do about tab characters.
     *
     * @return Behaviour for how this Screen should treat tab characters
     * @see TabBehaviour
     */
    TabBehaviour getTabBehaviour();

    /**
     * Sets the behaviour for what to do about tab characters.
     *
     * @param tabBehaviour Behaviour for how this Screen should treat tab characters
     * @see TabBehaviour
     */
    void setTabBehaviour(TabBehaviour tabBehaviour);

    /**
     * @return Size of the screen, in columns and rows
     */
    TerminalSize getTerminalSize();

    /**
     * Sets a character in the back-buffer to a specified value with specified colors and modifiers.
     * @param column Column of the character to modify (x coordinate)
     * @param row Row of the character to modify (y coordinate)
     * @param screenCharacter New data to put at the specified position
     */
    void setCharacter(int column, int row, ScreenCharacter screenCharacter);
    
    /**
     * Sets a character in the back-buffer to a specified value with specified colors and modifiers.
     * @param position Which position in the terminal to modify
     * @param screenCharacter New data to put at the specified position
     */
    void setCharacter(TerminalPosition position, ScreenCharacter screenCharacter);
    
    /**
     * Reads a character and its associated meta-data from the front-buffer and returns it encapsulated as a 
     * ScreenCharacter.
     * @param position What position to read the character from
     * @return A {@code ScreenCharacter} representation of the character in the front-buffer at the specified location
     */
    ScreenCharacter getFrontCharacter(TerminalPosition position);
    
    /**
     * Reads a character and its associated meta-data from the back-buffer and returns it encapsulated as a 
     * ScreenCharacter.
     * @param position What position to read the character from
     * @return A {@code ScreenCharacter} representation of the character in the back-buffer at the specified location
     */
    ScreenCharacter getBackCharacter(TerminalPosition position);

    /**
     * Reads the next {@code KeyStroke} from the input queue, or returns null if there is nothing on the queue. This
     * method will <b>not</b> block until input is available, you'll need to poll it periodically.
     * @return The KeyStroke that came from the underlying system, or null if there was no input available
     * @throws java.io.IOException In case there was a problem with the underlying input system
     */
    KeyStroke readInput() throws IOException;

    /**
     * This method will take the content from the back-buffer and move it into the front-buffer, making the changes
     * visible to the terminal in the process. The common workflow with Screen would involve drawing text and text-like
     * graphics on the back buffer and then finally calling refresh(..) to make it visible to the user.
     * @see RefreshType
     */
    void refresh() throws IOException;

    /**
     * This method will take the content from the back-buffer and move it into the front-buffer, making the changes
     * visible to the terminal in the process. The common workflow with Screen would involve drawing text and text-like
     * graphics on the back buffer and then finally calling refresh(..) to make it visible to the user.
     * <p/>
     * Using this method call instead of {@code refresh()} gives you a little bit more control over how the screen will
     * be refreshed.
     * @param refreshType What type of refresh to do
     * @see RefreshType
     */
    void refresh(RefreshType refreshType) throws IOException;

    /**
     * One problem working with Screens is that whenever the terminal is resized, the front and back buffers needs to be
     * adjusted accordingly and the program should have a chance to figure out what to do with this extra space (or less
     * space). The solution is to call, at the start of your rendering code, this method, which will check if the 
     * terminal has been resized and in that case update the internals of the Screen. After this call finishes, the 
     * screen's internal buffers will match the most recent size report from the underlying terminal.
     * 
     * @return If the terminal has been resized since this method was last called, it will return the new size of the
     * terminal. If not, it will return null.
     */
    TerminalSize doResizeIfNecessary();
    
    /**
     * This enum represents the different ways a Screen can refresh the screen, moving the back-buffer data into the
     * front-buffer that is being displayed.
     */
    public static enum RefreshType {
        
        AUTOMATIC,
        /**
         * In {@code RefreshType.DELTA} mode, the Screen will calculate a diff between the back-buffer and the 
         * front-buffer, then figure out the set of terminal commands that is required to make the front-buffer exactly
         * like the back-buffer. This normally works well when you have modified only parts of the screen, but if you
         * have modified almost everything it will cause a lot of overhead and you should use 
         * {@code RefreshType.COMPLETE} instead.
         */
        DELTA,
        /**
         * In {@code RefreshType.COMPLETE} mode, the screen will send a clear command to the terminal, when redraw the 
         * whole back-buffer line by line. This is more expensive than {@code RefreshType.COMPLETE}, especially when you
         * have only touched smaller parts of the screen, but can be faster if you have modified most of the content, 
         * as well as if you suspect the screen's internal front buffer is out-of-sync with what's really showing on the
         * terminal (you didn't go and call methods on the underlying Terminal while in screen mode, did you?)
         */
        COMPLETE,
        ;
    }
}
