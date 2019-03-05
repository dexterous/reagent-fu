(ns reagent-fu.components.material-ui
  (:require
    [reagent.core :as r]
    [goog.object :as gobj]
    ["@material-ui/core" :as mui]
    ["@material-ui/core/styles" :refer [createMuiTheme withStyles]]
    ["@material-ui/core/colors" :as mui-colors]
    ["@material-ui/icons" :as mui-icons]
    [recharts :as rc]))

(def custom-theme
  (createMuiTheme
    #js {:palette #js {:primary #js {:main (gobj/get (.-blue mui-colors) 100)}}}))

(defn custom-styles [theme]
  #js {:root #js {:flexGrow 1}
       :grow #js {:flexGrow 1 :textAlign "right"}
       :menuButton #js {:marginLeft -12
                        :marginRight 20}})

(def with-custom-styles (withStyles custom-styles))

(defn rand-data [x]
  (array-map :name        x 
             :open-close  [(+ 10 (rand-int 25)) (+ 50 (rand-int 25))]
             :min-max     [(+ 10 (rand-int 25)) (+ 50 (rand-int 25))]))

(defn mui-app [{:keys [classes appName] :as props}]
  (let [display (r/atom (js/Date.))
        chart-data (clj->js (map rand-data (range 31)))]
    (fn [{:keys [classes appName] :as props}]
      (js/setTimeout #(reset! display (js/Date.)) 1000)
      [:div {:class (.-root classes)}
       [:> mui/AppBar {:position "static"}
        [:> mui/Toolbar
         [:> mui/IconButton {:class (.-menuButton classes) :color "inherit" :aria-label "Menu"}
          [:> mui-icons/Menu]]
         [:> mui/Typography {:class (.-title classes) :variant "h5"} (str appName)]
         [:> mui/Typography {:class (.-grow classes) :variant "h6" :color "inherit"}
          (-> @display .toTimeString (.split " ") first)]
         [:> mui/Button {:color "inherit"}
          [:> mui-icons/Person] "Login"]]]
       [:> mui/Grid {:container true :direction "row" :justify "center"}
        [:> mui/Grid {:item true :xs 12}
         [:> rc/BarChart {:width 1000 :height 300 :layout "horizontal" :barGap 0 :barCategoryGap "15%" :data chart-data}
          [:> rc/CartesianGrid]
          [:> rc/XAxis {:dataKey "name"}]
          [:> rc/YAxis]
          [:> rc/Tooltip]
          [:> rc/Legend]
          [:> rc/Bar {:type "monotone" :dataKey "open-close" :fill "darkBlue"}]
          [:> rc/Bar {:type "monotone" :dataKey "min-max" :fill "orange"}]]]]])))

(defn main []
  ;; fragment
  [:<>
   [:> mui/CssBaseline]
   [:> mui/MuiThemeProvider {:theme custom-theme}
    [:> mui/Grid {:container true :direction "row" :justify "center"}
     [:> mui/Grid {:item true :xs 12}
      [:> (with-custom-styles (r/reactify-component mui-app)) {:appName "Hello!"}]]]]])
