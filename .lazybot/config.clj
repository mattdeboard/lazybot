(let [plugins #{"dictionary" "lmgtfy" "login"
                "help" "load" "yesno" "clojure" "google" "karma"
                "seen" "utils" "operator" "github"}]
  {:servers ["irc.freenode.net"]        ; A list of servers.
   :prepends #{"@"}   ; The character you want for a prepend. Currently set to @
   :weather {:token ""} ; Wunderground token.
   :dictionary {:wordnik-key
                "99c266291da87b231f40a0c8902040da0b568588c25526cff"} 
   :sed {:automatic? true}
   ;; The maximum number of operations that can be running at any given time.   
   :max-operations 3
   ;; The number of operations running right now
   :pending-ops 0
   :prefix-arrow "\u21D2 "
   :help {:admin-add? true  ; only admins can add help topics
          :admin-rm? true}   ; only admins can remove help topics
   :clojure {:eval-prefixes {:defaults ["->" "." "," ; prefixes in any channel
                                        ;; recognize multi-char prefixes
                                        ;; starting with `&`
                                        #"&\|(.*?)(?=\|&|\|&|$)"
                                        ;; multi-char prefixes starting with
                                        ;; `##`
                                        #"##(([^#]|#(?!#))+)\s*((##)?(?=.*##)|$)"]
                             ;; list of prefixes NOT to use in certain channels
                             ;;"#tempchan" ["->"]   ; turn this off for testing
                             ;;"#clojure" [","]}}    ; let clojurebot have this one
                             }}
   :servers-port 21310                  ; port for plugins that require a webserver
   :github {:commits {"https://github.com/mattdeboard/lazybot" {"irc.freenode.net" ["#postfarm"]}}}
   "irc.freenode.net" {:channels ["#postfarm"]
                       :bot-name "pfbot"
                       :sed {:blacklist #{"#tempchan2"}}
                       :bot-password nil
                       :users (read-string (slurp "/home/matt/lazybot/.lazybot/secret.clj")) 
                       :title {:blacklist #{"#foo"}}
                       :autoreply {:autoreplies {"#clojure" {#".*(https?://)richhickey(.github.com/\S*).*" "Nooooo, that's so out of date! Please see instead $1clojure$2 and try to stop linking to rich's repo."}}}
                       :plugins plugins}})

; users is a series of username to password and privileges.
; plugins is a list of plugins to load at startup.
