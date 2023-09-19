(defproject com.github.igrishaev/with-http "0.1.0-SNAPSHOT"

  :description
  "FIXME: write description"

  :url "http://example.com/FIXME"

  :license
  {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
   :url "https://www.eclipse.org/legal/epl-2.0/"}

  :deploy-repositories
  {"releases" {:url "https://repo.clojars.org" :creds :gpg}}

  :release-tasks
  [["vcs" "assert-committed"]
   ["test"]
   ["change" "version" "leiningen.release/bump-version" "release"]
   ["vcs" "commit"]
   ["vcs" "tag" "--no-sign"]
   ["deploy"]
   ["change" "version" "leiningen.release/bump-version"]
   ["vcs" "commit"]
   ["vcs" "push"]]

  :dependencies
  [[org.clojure/tools.logging "1.2.4"]
   [ring/ring-json "0.5.0"]
   [ring/ring-core "1.7.1"]
   [ring/ring-jetty-adapter "1.7.1"]]

  :profiles
  {:dev
   {:dependencies
    [[org.clojure/clojure "1.11.1"]]}
   :test
   {:dependencies
    [[clj-http "3.12.3"]]}})
