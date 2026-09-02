package turtley;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the application facade used by both the command-line and graphical interfaces.
 */
class TurtleyTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_addTask_returnsResponseAndPersistsTask() {
        Turtley turtley = createTurtley();

        String response = turtley.getResponse("todo read book");

        assertEquals(String.join(System.lineSeparator(),
                "Got it. I've added this task:",
                "  [T][ ] read book",
                "Now you have 1 tasks in the list."), response);
        assertTrue(turtley.getResponse("list").contains("1.[T][ ] read book"));
    }

    @Test
    void getResponse_list_returnsTasksWithoutConsoleSeparators() {
        Turtley turtley = createTurtley();
        turtley.getResponse("todo read book");

        String response = turtley.getResponse("list");

        assertEquals(String.join(System.lineSeparator(),
                "Here are the tasks in your list:",
                " 1.[T][ ] read book"), response);
    }

    @Test
    void getResponse_invalidCommand_returnsUserFacingError() {
        Turtley turtley = createTurtley();

        String response = turtley.getResponse("unknown command");

        assertEquals("Please input something correct. o/T" + (char) 92 + ">", response);
    }

    @Test
    void getResponse_exit_returnsGoodbyeMessage() {
        Turtley turtley = createTurtley();

        String response = turtley.getResponse("bye");

        assertEquals("Bye. See you around! o/T" + (char) 92 + ">", response);
    }

    private Turtley createTurtley() {
        Path saveFile = temporaryDirectory.resolve("turtley.txt");
        return new Turtley(saveFile.toString());
    }
}
