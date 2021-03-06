(ns lazybot.plugins.utils
  (:use [lazybot utilities info registry]
        [lazybot.plugins.login :only [when-privs]]
        [lazybot.gist :only [trim-with-gist]]
	[clj-time.core :only [plus minus now interval in-secs hours]]
        [clj-time.format :only [unparse formatters]]
        [clojure.java.shell :only [sh]])
  (:require [irclj.core :as ircb]
            [clojure.string :as s])
  (:import java.net.InetAddress))

(defn pangram? [s]
  (let [letters (into #{} "abcdefghijklmnopqrstuvwxyz")]
    (= (->> s .toLowerCase (filter letters) (into #{})) letters)))

(defplugin
  (:cmd
   "Gets the current time and date in UTC format."
   #{"time"} 
   (fn [{:keys [nick bot args] :as com-m}]
     (let [time (unparse (formatters :date-time-no-ms) 
                         (if-let [[[m num]] (seq args)]
			 
                           (let [n (try (Integer/parseInt (str num)) (catch Exception _ 0))] 
                             (condp = m
                                 \+ (plus (now) (hours n))
                                 \- (minus (now) (hours n))
                                 (now)))
                           (now)))]
       (send-message com-m (prefix nick "The time is now " time)))))

  (:cmd
   "Joins a channel. Takes a channel and an optional password. ADMIN ONLY."
   #{"join"}
   (fn [{:keys [com bot nick args] :as com-m}]
     (when-privs com-m :admin
               (ircb/join-chan com (first args) (last args)))))

  (:cmd
   "Parts a channel. Takes a channel and a part message. ADMIN ONLY." 
   #{"part"}
   (fn [{:keys [bot com nick args channel] :as com-m}]
     (when-privs com-m :admin
               (let [chan (or (first args) channel)]
                 (send-message com-m "Bai!")
                 (ircb/part-chan com chan :reason "Quit")))))

  (:cmd
   "Rapes a person you specify."
   #{"rape"}
   (fn [{:keys [args] :as com-m}]
     (send-message com-m (str "raepz " (first args) ".") :action? true)))

  (:cmd 
   "Flips a coin."
   #{"coin"} 
   (fn [{:keys [bot nick] :as com-m}]
     (send-message com-m (prefix nick (if (= 0 (rand-int 2)) "Heads." "Tails.")))))

  (:cmd 
   "Prints an amusing message."
   #{"what"} 
   (fn [com-m] (send-message com-m "It's AWWWW RIGHT!")))
   
  (:cmd 
   "Checks if its input string is a pangram."
   #{"pangram?"} 
   (fn [{:keys [args] :as com-m}]
     (send-message com-m (->> args s/join pangram? str))))
   
  (:cmd 
   "Just says the sender's name: no u."
   #{"fuck"} 
   (fn [{:keys [bot nick] :as com-m}]
     (send-message com-m (prefix nick "no u"))))

  (:cmd
   "Sets the bot's nick. ADMIN ONLY."
   #{"setnick"} 
   (fn [{:keys [com bot nick args] :as com-m}]
     (when-privs com-m :admin (ircb/set-nick com (first args)))))

  (:cmd
   "Amusing command to check to see if a directory exists on the system that runs the bot."
   #{"exists?"} 
   (fn [{:keys [args] :as com-m}]
     (send-message com-m (str (.exists (java.io.File. (first args)))))))

  (:cmd
   "Love your bot? Give him a snack and thank him for his hard work!"
   #{"botsnack"} 
   (fn [{:keys [nick bot] :as com-m}]
     (send-message com-m (prefix nick "Thanks! Om nom nom!!"))))
   
  (:cmd
   "Prints an amusing and inappropriate message directed at a person you specify. For when people
    use 'your' when they should have used 'you're'"
   #{"your"}
   (fn [{:keys [args] :as com-m}]
     (send-message com-m (str (first args) ": It's 'you're', you fucking illiterate bastard."))))

  (:cmd 
   "Prints an amusing message."
   #{"kill"}
   (fn [com-m] (send-message com-m "KILL IT WITH FIRE!")))

   (:cmd 
    "Balances parens for you."
    #{"balance"}
    (fn [{:keys [nick args bot] :as com-m}]
      (let [fst (first args)]
        (send-message com-m
                      (prefix nick (apply str (concat fst (repeat (count fst) ")"))))))))

  (:cmd 
   "Says what you tell it to in the channel you specify. ADMIN ONLY."
   #{"say"} 
   (fn [{:keys [bot nick args] :as com-m}]
     (when-privs com-m :admin
               (send-message (assoc com-m :channel (first args))
                             (->> args rest (interpose " ") (apply str))))))

  (:cmd
   "Temperature conversion. If given Cn, converts from C to F. If given Fn, converts from F to C."
   #{"tc" "tempconv"}
   (fn [{:keys [nick args bot] :as com-m}]
     (let [num (->> args first rest (apply str) Integer/parseInt)]
       (send-message com-m 
                     (prefix nick
                             (condp = (ffirst args)
                                 \F (* (- num 32) (/ 5 9.0))
                                 \C (+ 32 (* (/ 9.0 5) num))
                                 "Malformed expression."))))))
  
  (:cmd
   "Pings an IP address or host name. If it doesn't complete within 10 seconds, it will give up."
   #{"ping"}
   (fn [{:keys [bot nick args] :as com-m}]
     (let [address (InetAddress/getByName (first args))
           stime (now)]
       (send-message 
        com-m
        (prefix nick
             (if (= false (.isReachable address 5000))
               "FAILURE!"
               (str "Ping completed in " (in-secs (interval stime (now))) " seconds.")))))))

  (:cmd
   "Huggles your best fwiendz."
   #{"huggle"}
   (fn [{:keys [args] :as com-m}]
     (send-message com-m (str "Hugglez " (first args) ". I lubs yous.") :action? true)))

  (:cmd
   "I'd do you."
   #{"would"}
   (fn [com-m] (send-message com-m "I'd do him. Hard.")))

  (:cmd
   "Executes a shell command and displays the STDOUT"
   #{"shell"}
   (fn [{:keys [bot nick args] :as com-m}]
     (when-privs com-m :admin
      (send-message
       com-m
       (let [cmd (s/join " " args)]
         (trim-with-gist cmd
           (s/replace
            (:out (sh "bash" "-c" cmd)) #"\s+" " "))))))))
