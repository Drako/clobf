(defproject guru.drako.example/clobf "1.0.0-SNAPSHOT"
  :description "A simple Brainfuck interpreter in Clojure"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/tools.nrepl "0.2.12"]]
  :main guru.drako.example.clobf.core
  :profiles {:uberjar {:aot :all}}
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "same as Closure"})

