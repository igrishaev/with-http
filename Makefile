
all: test

release:
	lein release

repl:
	lein with-profile +test repl

.PHONY: test
test:
	lein test
