
all: test

release:
	lein release

repl:
	lein with-profile +test repl

.PHONY: test
test:
	lein test

toc-install:
	npm install --save markdown-toc

toc-build:
	node_modules/.bin/markdown-toc -i README.md
