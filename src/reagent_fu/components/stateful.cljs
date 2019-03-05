(ns reagent-fu.components.stateful
  (:require [reagent.core :as r]))

; stateful component
(defn number-picker
  ([]
   [number-picker 0])
  ([n]
   (let [value (if (integer? n) (r/atom n) n)]
     (fn []
       [:p
        [:input {:type "button" :value "-" :on-click #(swap! value dec)}]
        [:input {:type "text" :value @value :read-only true}]
        [:input {:type "button" :value "+" :on-click #(swap! value inc)}]]))))
