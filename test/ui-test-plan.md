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
    }
  ]
}
```
