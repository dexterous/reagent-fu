(ns reagent-fu.components.material-ui
  (:require
    [clojure.string :as s]
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

(defn day [timestamp]
  (-> (js/Date. (* timestamp 1000))
      (.toDateString)
      (s/split #" ")
      (->> (rest)
           (take 2)
           (reverse)
           (s/join " "))))

(defn stock-data [data]
  (let [result (get-in data [:chart :result 0])
        quotes (get-in result [:indicators :quote 0])]
    (sort-by :timestamp
             (map
               (fn [timestamp open close high low volume]
                 {:timestamp timestamp
                  :date (day timestamp)
                  :open-close [open close]
                  :min-max [(- close low) (- high close)]
                  :volume volume})
               (get-in result [:timestamp])
               (get-in quotes [:open])
               (get-in quotes [:close])
               (get-in quotes [:high])
               (get-in quotes [:low])
               (get-in quotes [:volume])))))

(defn cell [e]
  (let [[open close] (:open-close e)]
    [(r/adapt-react-class rc/Cell) {:key (:date e) :fill (if (< open close) "blue" "gray")}]))

(defn chart [json-data]
  (let [chart-data (stock-data @json-data)]
    [:> rc/ComposedChart {:width 1200 :height 300 :layout "horizontal" :barGap 0 :barCategoryGap "15%" :data (clj->js chart-data)}
     [:> rc/CartesianGrid]
     [:> rc/XAxis {:dataKey "date"}]
     [:> rc/YAxis {:yAxisId "price" :domain (clj->js [#(- (Math/floor %) 5) #(+ (Math/ceil %) 5)])}]
     [:> rc/YAxis {:yAxisId "volume" :orientation "right"}]
     [:> rc/Tooltip]
     [:> rc/Bar {:dataKey "open-close" :yAxisId "price"}
      [:> rc/ErrorBar {:dataKey "min-max" :stroke "orange"}]
      (map cell chart-data)]
     [:> rc/Line {:dataKey "volume" :stroke "green" :type "natural" :dot false :yAxisId "volume"}]]))

(defn mui-app [{:keys [classes appName jsonData] :as props}]
  (let [display (r/atom (js/Date.))]
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
         [chart jsonData]]]])))

(defn main []
  (let [json-data (r/atom {})]
    (-> "./data.json"
        (js/fetch)
        (.then (fn [r] (.json r)))
        (.then (fn [j] (swap! json-data (constantly (js->clj j :keywordize-keys true))))))
    [:<> ;; fragment 
     [:> mui/CssBaseline]
     [:> mui/MuiThemeProvider {:theme custom-theme}
      [:> mui/Grid {:container true :direction "row" :justify "center"}
       [:> mui/Grid {:item true :xs 12}
        [:> (with-custom-styles (r/reactify-component mui-app)) {:appName "Hello!" :jsonData json-data}]]]]]))
