(ns guru.drako.example.clobf.core
  (:gen-class))

(def buffer-size 65536)
(def initial-state
  {:ptr 0
   :mem (vec (repeat buffer-size 0))})

(defn inc-val
  "increment the value at ptr"
  [state]
  (update-in state [:mem (:ptr state)] inc))

(defn dec-val
  "decrement the value at ptr"
  [state]
  (update-in state [:mem (:ptr state)] dec))

(defn inc-ptr
  "increment ptr"
  [state]
  (update state :ptr #(mod (inc %) buffer-size)))

(defn dec-ptr
  "increment ptr"
  [state]
  (update state :ptr #(mod (dec %) buffer-size)))

(defn print-val
  "prints the value at ptr as ascii"
  [state]
  (print (char (get-in state [:mem (:ptr state)])))
  state)

(defn make-scope
  "creates a closure applying an entire scope on the state"
  [instructions]
  #(reduce (fn [state instruction] (instruction state)) % instructions))

(defn make-loop
  "creates a loop around a scope"
  [instructions]
  (let
    [scope (make-scope instructions)]
    #(loop [state %]
       (if (zero? (get-in state [:mem (:ptr state)]))
         state
         (recur (scope state))))))

(defn read-char
  "reads a single character from java.io.Reader"
  [stream]
  (.read stream))

(defn make-ast
  "creates an abstract syntax tree from a java.io.Reader"
  [stream]
  (loop [ast [] cc (read-char stream)]
    (if (= cc -1)
      (throw (IllegalStateException. "Missing closing bracket."))
      (if (= (char cc) \])
        ast
        (recur
          (case (char cc)
            \+ (conj ast inc-val)
            \- (conj ast dec-val)
            \> (conj ast inc-ptr)
            \< (conj ast dec-ptr)
            \. (conj ast print-val)
            \[ (conj ast (make-loop (make-ast stream)))
            ast)
          (read-char stream))))))

(defn interpret-file
  [stream]
  (loop [state initial-state cc (read-char stream)]
    (if (= cc -1)
      state
      (if (= (char cc) \])
        (throw (IllegalStateException. "Unexpected closing bracket."))
        (recur
          (case (char cc)
            \+ (inc-val state)
            \- (dec-val state)
            \> (inc-ptr state)
            \< (dec-ptr state)
            \. (print-val state)
            \[ ((make-loop (make-ast stream)) state)
            state)
          (read-char stream))))))

(defn -main
  "entry point"
  [source-file]
  (with-open [stream (clojure.java.io/reader source-file)]
    (try
      (interpret-file stream)
      (catch IllegalStateException ex
        (println "An error occurred: " (.getMessage ex))))
    (flush)))
