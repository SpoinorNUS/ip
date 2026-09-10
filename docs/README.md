# Turtley User Guide

Turtley supports to-do, deadline, and event tasks. Tags can be attached to any
task and are shown at the end of the task.

## Adding tasks with tags

Tags are optional and must be supplied using the trailing /tag modifier.
Each tag starts with '#', contains 1-10 non-whitespace characters, and a task
can have at most 10 tags.

```
todo go jogging /tag #fun #health
deadline submit report /by 2026-12-31 /tag #work
event team dinner /from 2026-09-20 19:00 /to 2026-09-20 21:00 /tag #social
```

The tags are extracted from the description and displayed alphabetically:

```
[T][ ] go jogging [#fun] [#health]
```

Tags are case-sensitive, so `#Fun` and `#fun` are different tags. Duplicate
tags are ignored.

## Tagging existing tasks

```
tag <task number> #tag1 [#tag2 ...]
untag <task number> #tag1 [#tag2 ...]
```

For example:

```
tag 1 #fun #weekend
untag 1 #weekend
```

Tagging is allowed for completed tasks. Removing a tag that is not present is
ignored. Invalid commands do not modify the task.

## Searching and filtering

'find' searches both descriptions and tags using case-insensitive substring
matching:

```
find fun
```

'filter' searches tags only. The filter query must start with '#' and uses
case-insensitive substring matching:

```
filter #fun
```

This matches tags such as '#fun' and '#funny'.

## Help

Use 'help' to display the complete command reference and tagging rules.
