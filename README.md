This is a small and simple implementation of a small and simple wiew of the world:

- The world contains things, and relations between things which are also things.

- The things can have a value, represented by a short string.

There are artificial feaures of the model as well:

- The things can have a type, which is also a thing.

- Things should be identifiable, so they all should have an id, which is a value.

- As the world view of one can change over time, things can also have a version (again a value).

This world view is simple to the extent of unuseability. So other features can be
implemented in it, e.g. to have a useful ontology, a platform for collaboration,
or anything you like. (The feature of implemented features is not implemented yet though:)

The project is at github:

https://github.com/magwas/worldmodel/

The kanban table:

http://huboard.com/magwas/worldmodel/board


Building:

1. you should make sure that java uses UTF-8 as encoding. There are more ways to do it:

 - your LANG environment variable is set accordingly, e.g. export LANG=en\_US.UTF-8

 - export JAVA_TOOL_OPTIONS="$JAVA_TOOL_OPTIONS -Dfile.encoding=UTF8"

 - with jenkins use the Environment Injector plugin with the following Groovy Script:

        def map = [JAVA_TOOL_OPTIONS: " -Dfile.encoding=UTF8"]
        return


2. You should have phantomjs installed. You need >= 1.5 if you want to compile headless (e.g. from jenkins)


Code review:

Code review is done with gerrit..

