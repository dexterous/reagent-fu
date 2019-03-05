(ns reagent-fu.components.stateless)

(defn sample-component []
  [:div.awesome
   [:p.bold "This is a sample component"]
   [:p#my-p.totally.awesome "We have now wrapped it's content in a <div />"]])

