(defproject app "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[buddy "2.0.0"]
                 [cc.qbits/alia-all "4.3.7-beta1"]
                 [compojure "1.7.1"]
                 [metosin/jsonista "0.3.8"]
                 [org.clojure/clojure "1.11.1"]
                 [org.slf4j/slf4j-simple "2.0.12"]
                 [ring/ring-core "1.12.0"]
                 [ring/ring-jetty-adapter "1.8.2"]
                 [ring/ring-json "0.5.0"]
                 [jumblerg/ring-cors "3.0.0"]]
  :plugins [[lein-cloverage "1.2.2"]]
  :main ^:skip-aot adapter.driver.api.server
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
