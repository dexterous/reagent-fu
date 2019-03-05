(ns reagent-fu.core
  (:require
    [reagent.core :as r]
    [reagent-fu.components.stateless :as comp-sl]
    [reagent-fu.components.stateful :as comp-sf]
    [reagent-fu.components.timed :as comp-t]))

(enable-console-print!)

(println "This is new!")

; compound app
(defn app []
  (let [cap (r/atom 10)]
    [:div.awesome
     [comp-t/clock]
     [:hr {:width "90%"}]
     [comp-sf/number-picker cap]
     [comp-t/timer cap]
     [:hr {:width "80%"}]
     [comp-sl/sample-component]]))

; app runner
(defn ^:export run []
  (r/render
    [app]
    (.getElementById js/document "app")))
