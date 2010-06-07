(ns sexpbot.run
  (:use [sexpbot core respond]))

(def bots (ref {}))



(require-plugins)

(doseq [server (:servers info)]
  (let [bot (make-bot server)]
    (dosync (alter bots assoc server bot))
    (load-plugins bot)
    (doseq [plug (:plugins info)] (load-modules bot))))
