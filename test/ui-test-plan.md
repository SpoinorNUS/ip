# UI Test Plan

This plan covers the command-line interaction documented for Turtley. Each test case starts a fresh program session. The `inputs` and `expected_outputs` arrays in the JSON block are aligned by position: each expected output must appear after the corresponding input's earlier output.

## Test case 1: To-do, deadline, and event workflow

Aim: Verify that ordinary to-dos, deadlines, and events are accepted, displayed with the correct type icons and details, retained in insertion order, and counted correctly.

Inputs:

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
todo join sports club
mark 1
mark 4
todo borrow book
list
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
bye
```

Expected output: The program prints the corresponding confirmation for each command, including the following important results:

```text
[T][ ] borrow book
[D][ ] return book (by: June 6th)
[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
1.[T][X] read book
2.[D][ ] return book (by: June 6th)
3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
4.[T][X] join sports club
5.[T][ ] borrow book
[D][ ] return book (by: Sunday)
[E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 7 tasks in the list.
```

The executable expected-output list is kept below so the `test-ui` skill can run this case without guessing which output belongs to each command.

```json test-ui
{
  "test_cases": [
    {
      "name": "To-do, deadline, and event workflow",
      "aim": "Verify creation, formatting, listing, completion status, and task counts for all supported task types.",
      "inputs": [
        "todo read book",
        "deadline return book /by June 6th",
        "event project meeting /from Aug 6th 2pm /to 4pm",
        "todo join sports club",
        "mark 1",
        "mark 4",
        "todo borrow book",
        "list",
        "deadline return book /by Sunday",
        "event project meeting /from Mon 2pm /to 4pm",
        "bye"
      ],
      "expected_outputs": [
        "Got it. I've added this task:\n  [T][ ] read book\nNow you have 1 tasks in the list.",
        "Got it. I've added this task:\n  [D][ ] return book (by: June 6th)\nNow you have 2 tasks in the list.",
        "Got it. I've added this task:\n  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)\nNow you have 3 tasks in the list.",
        "Got it. I've added this task:\n  [T][ ] join sports club\nNow you have 4 tasks in the list.",
        "Nice! I've marked this task as done:\n   [X] read book",
        "Nice! I've marked this task as done:\n   [X] join sports club",
        "Got it. I've added this task:\n  [T][ ] borrow book\nNow you have 5 tasks in the list.",
        "Here are the tasks in your list:\n 1.[T][X] read book\n 2.[D][ ] return book (by: June 6th)\n 3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)\n 4.[T][X] join sports club\n 5.[T][ ] borrow book",
        "Got it. I've added this task:\n  [D][ ] return book (by: Sunday)\nNow you have 6 tasks in the list.",
        "Got it. I've added this task:\n  [E][ ] project meeting (from: Mon 2pm to: 4pm)\nNow you have 7 tasks in the list.",
        "Bye. See you around!"
      ]
    },
    {
      "name": "To-do without a name",
      "aim": "Verify that missing to-do names are rejected without adding empty tasks.",
      "inputs": [
        "todo",
        "list",
        "bye"
      ],
      "expected_outputs": [
        "Invalid format. Use: todo <description> o/T\\>",
        "Task list empty. Good job! Here's a cookie. o/T\\>",
        "Bye. See you around!"
      ]
    },
    {
      "name": "Error messages",
      "aim": "Verify that invalid commands, task numbers, and structured task formats use the custom Turtley error message suffix.",
      "inputs": [
        "deadline /by tomorrow",
        "event /from 2pm /to 3pm",
        "mark nope",
        "unmark nope",
        "mark 1",
        "unmark 1",
        "",
        "unknown command",
        "bye"
      ],
      "expected_outputs": [
        "Invalid format. Use: deadline <description> /by <date> o/T\\>",
        "Invalid format. Use: event <description> /from <start> /to <end> o/T\\>",
        "Please provide a valid task number. o/T\\>",
        "Please provide a valid task number. o/T\\>",
        "Task number is not in your list. o/T\\>",
        "Task number is not in your list. o/T\\>",
        "Please input something. o/T\\>",
        "Please input something correct. o/T\\>",
        "Bye. See you around!"
      ]
    },
    {
      "name": "Delete task workflow",
      "aim": "Verify that a task can be deleted by its one-based number, that its details are shown, that the count decreases, and that later tasks are renumbered.",
      "inputs": [
        "todo read book",
        "deadline return book /by June 6th",
        "event project meeting /from Aug 6th 2pm /to 4pm",
        "todo join sports club",
        "todo borrow book",
        "mark 1",
        "mark 2",
        "list",
        "delete 3",
        "list",
        "bye"
      ],
      "expected_outputs": [
        "Got it. I've added this task:\n  [T][ ] read book\nNow you have 1 tasks in the list.",
        "Got it. I've added this task:\n  [D][ ] return book (by: June 6th)\nNow you have 2 tasks in the list.",
        "Got it. I've added this task:\n  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)\nNow you have 3 tasks in the list.",
        "Got it. I've added this task:\n  [T][ ] join sports club\nNow you have 4 tasks in the list.",
        "Got it. I've added this task:\n  [T][ ] borrow book\nNow you have 5 tasks in the list.",
        "Nice! I've marked this task as done:\n   [X] read book",
        "Nice! I've marked this task as done:\n   [X] return book",
        "Here are the tasks in your list:\n 1.[T][X] read book\n 2.[D][X] return book (by: June 6th)\n 3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)\n 4.[T][ ] join sports club\n 5.[T][ ] borrow book",
        "Noted. I've removed this task:\n   [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)\n Now you have 4 tasks in the list.",
        "Here are the tasks in your list:\n 1.[T][X] read book\n 2.[D][X] return book (by: June 6th)\n 3.[T][ ] join sports club\n 4.[T][ ] borrow book",
        "Bye. See you around!"
      ]
    }
  ]
}
```

## Test case 2: To-do without a name

Aim: Verify that a to-do command without a name is rejected without adding an empty task.

Inputs:

```text
todo
list
bye
```

Expected output:

```text
Invalid format. Use: todo <description> o/T\>
Task list empty. Good job! Here's a cookie. o/T\>
```

## Test case 3: Error messages

Aim: Verify that invalid commands, task numbers, and structured task formats use the custom Turtley error message suffix.

Inputs:

```text
deadline /by tomorrow
event /from 2pm /to 3pm
mark nope
unmark nope
mark 1
unmark 1

unknown command
bye
```

Expected output:

```text
Invalid format. Use: deadline <description> /by <date> o/T\>
Invalid format. Use: event <description> /from <start> /to <end> o/T\>
Please provide a valid task number. o/T\>
Please provide a valid task number. o/T\>
Task number is not in your list. o/T\>
Task number is not in your list. o/T\>
Please input something. o/T\>
Please input something correct. o/T\>
Bye. See you around!
```

## Test case 4: Delete task workflow

Aim: Verify that a task is deleted by its one-based number, its details and updated task count are shown, and later tasks are renumbered.

Inputs:

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
todo join sports club
todo borrow book
mark 1
mark 2
list
delete 3
list
bye
```

Expected output for the relevant commands:

```text
 Here are the tasks in your list:
 1.[T][X] read book
 2.[D][X] return book (by: June 6th)
 3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 4.[T][ ] join sports club
 5.[T][ ] borrow book
 Noted. I've removed this task:
   [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 Now you have 4 tasks in the list.
 1.[T][X] read book
 2.[D][X] return book (by: June 6th)
 3.[T][ ] join sports club
 4.[T][ ] borrow book
```
