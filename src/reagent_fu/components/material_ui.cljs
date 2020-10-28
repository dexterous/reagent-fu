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
  (array-map :date        x 
             :open-close  [(+ 10 (rand-int 25)) (+ 50 (rand-int 25))]
             :min-max     [(- 50 (rand-int 10)) (+ 0 (rand-int 10))] :foo [0 0]
             :volume      (rand-int 25)))

(def json-str
  (str
    "{\"chart\":{\"result\":[{\"meta\":{\"currency\":\"USD\",\"symbol\":\"MSFT\",\"exchangeName\":\"NMS\",\"instrumentType\":\"EQUITY\",\"firstTradeDate\":511108200,\"regularMarketTime\":1603396802,\"gmtoffset\":-14400,\"timezone\":\"EDT\",\"exchangeTimezoneName\":\"America/New_York\",\"regularMarketPrice\":214.89,\"chartPreviousClose\":207.42,\"priceHint\":2,\"currentTradingPeriod\":{\"pre\":{\"timezone\":\"EDT\",\"start\":1603353600,\"end\":1603373400,\"gmtoffset\":-14400},\"regular\":{\"timezone\":\"EDT\",\"start\":1603373400,\"end\":1603396800,\"gmtoffset\":-14400},\"post\":{\"timezone\":\"EDT\",\"start\":1603396800,\"end\":1603411200,\"gmtoffset\":-14400}},\"dataGranularity\":\"1d\",\"range\":\"1mo\",\"validRanges\":[\"1d\",\"5d\",\"1mo\",\"3mo\",\"6mo\",\"1y\",\"2y\",\"5y\",\"10y\",\"ytd\",\"max\"]},\"timestamp\":[1600867800,1600954200,1601040600,1601299800,1601386200,1601472600,1601559000,1601645400,1601904600,1601991000,1602077400,1602163800,1602250200,1602509400,1602595800,1602682200,1602768600,1602855000,1603114200,1603200600,1603287000,1603373400],\"indicators\":{\"quote\":[{\"open\":[207.89999389648438,199.85000610351562,203.5500030517578,210.8800048828125,209.35000610351562,207.72999572753906,213.49000549316406,208,207.22000122070312,208.82000732421875,207.05999755859375,210.50999450683594,211.22999572753906,218.7899932861328,222.72000122070312,223,217.10000610351562,220.14999389648438,220.4199981689453,215.8000030517578,213.1199951171875,213.92999267578125],\"high\":[208.10000610351562,205.57000732421875,209.0399932861328,212.57000732421875,210.07000732421875,211.97999572753906,213.99000549316406,210.99000549316406,210.41000366210938,210.17999267578125,210.11000061035156,211.19000244140625,215.86000061035156,223.86000061035156,225.2100067138672,224.22000122070312,220.36000061035156,222.2899932861328,222.3000030517578,217.3699951171875,216.9199981689453,216.05999755859375],\"volume\":[30803800,31202500,29437300,32004900,24221900,33780700,27158400,33154800,21331600,28554300,25681100,19925800,26458000,40461400,28950800,23451700,22733100,26057900,27625800,22753500,22724900,22334100],\"low\":[200.02999877929688,199.1999969482422,202.5399932861328,208.05999755859375,206.80999755859375,206.5399932861328,211.32000732421875,205.5399932861328,206.97999572753906,204.82000732421875,206.72000122070312,208.32000732421875,211.22999572753906,216.80999755859375,220.42999267578125,219.1300048828125,216.00999450683594,219.32000732421875,213.72000122070312,213.08999633789062,213.1199951171875,211.6999969482422],\"close\":[200.58999633789062,203.19000244140625,207.82000732421875,209.44000244140625,207.25999450683594,210.3300018310547,212.4600067138672,206.19000244140625,210.3800048828125,205.91000366210938,209.8300018310547,210.5800018310547,215.80999755859375,221.39999389648438,222.86000061035156,220.86000061035156,219.66000366210938,219.66000366210938,214.22000122070312,214.64999389648438,214.8000030517578,214.88999938964844]}]"
    ",\"adjclose\":[{\"adjclose\":[200.58999633789062,203.19000244140625,207.82000732421875,209.44000244140625,207.25999450683594,210.3300018310547,212.4600067138672,206.19000244140625,210.3800048828125,205.91000366210938,209.8300018310547,210.5800018310547,215.80999755859375,221.39999389648438,222.86000061035156,220.86000061035156,219.66000366210938,219.66000366210938,214.22000122070312,214.64999389648438,214.8000030517578,214.88999938964844]}]}}],\"error\":null}}"))

(defn day [timestamp]
  (let [d (js/Date. (* timestamp 1000))]
    (str (.getDate d) "/" (inc (.getMonth d)))))

(defn stock-data []
  (let [data (js->clj (.parse js/JSON json-str) :keywordize-keys true)
        result (get-in data [:chart :result 0])
        quotes (get-in result [:indicators :quote 0])]
    (sort-by :timestamp
             (map
               (fn [timestamp open close high low volume]
                 {:timestamp timestamp
                  :date (day timestamp)
                  :open-close [open close]
                  :min-max [(- close low) (- high close)] :foo [0 0]
                  :volume volume})
               (get-in result [:timestamp])
               (get-in quotes [:open])
               (get-in quotes [:close])
               (get-in quotes [:high])
               (get-in quotes [:low])
               (get-in quotes [:volume])))))

(defn cell [e]
  (let [e (js->clj e :keywordize-keys true)
        [open close] (:open-close e)
        cell-key (:date e)]
    (with-meta
      [:> rc/Cell {:key cell-key :fill (if (< open close) "blue" "gray")}]
      {:key cell-key})))

(defn chart [chart-data]
  [:> rc/ComposedChart {:width 1200 :height 300 :layout "horizontal" :barGap 0 :barCategoryGap "15%" :data chart-data}
   [:> rc/CartesianGrid]
   [:> rc/XAxis {:dataKey "date"}]
   [:> rc/YAxis {:yAxisId "price" :domain (clj->js [#(- (Math/floor %) 5) #(+ (Math/ceil %) 5)])}]
   [:> rc/YAxis {:yAxisId "volume" :orientation "right"}]
   [:> rc/Tooltip]
   ;[:> rc/Legend]
   [:> rc/Bar {:dataKey "open-close" :yAxisId "price"}
    ;[:> rc/ErrorBar {:dataKey "foo" :stroke "black"}] ;close marker
    [:> rc/ErrorBar {:dataKey "min-max" :stroke "orange"}]
    (map cell chart-data)]
   [:> rc/Line {:dataKey "volume" :stroke "green" :type "natural" :dot false :yAxisId "volume"}]])

(defn mui-app [{:keys [classes appName] :as props}]
  (let [display (r/atom (js/Date.))
        ;chart-data (clj->js (map rand-data (range 31)))
        chart-data (clj->js (stock-data))]
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
         [chart chart-data]]]])))

(defn main []
  ;; fragment
  [:<>
   [:> mui/CssBaseline]
   [:> mui/MuiThemeProvider {:theme custom-theme}
    [:> mui/Grid {:container true :direction "row" :justify "center"}
     [:> mui/Grid {:item true :xs 12}
      [:> (with-custom-styles (r/reactify-component mui-app)) {:appName "Hello!"}]]]]])
