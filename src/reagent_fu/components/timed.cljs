(ns reagent-fu.components.timed
  (:require [reagent.core :as r]))

; timer based components
(defn timer [cycle-at]
  (let [display (r/atom 0)
        bump #(-> % inc (mod @cycle-at))
        ticker (js/setInterval #(swap! display bump) 1000)]
    (fn []
      [:div.awesome
       [:p "You have been here " @display " seconds."]
       [:input {:type "button" :value "Stop"  :on-click #(js/clearInterval ticker)}]
       [:input {:type "button" :value "Reset" :on-click #(reset! display 0)}]])))

(defn clock []
  (let [display (r/atom (js/Date.))]
    (fn []
      (js/setTimeout #(reset! display (js/Date.)) 1000)
      [:div.bright {:style {:text-align "right" :padding-right "50px"}} (-> @display .toTimeString (.split " ") first)])))
