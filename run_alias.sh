alias simccs='cd ~/Projects/SimCCS-public && java \
  --module-path ~/javafx/javafx-sdk-25.0.1/lib \
  --add-modules javafx.controls,javafx.fxml \
  --add-exports=javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED \
  --enable-native-access=javafx.graphics \
  -jar ~/Projects/SimCCS-public/store/SimCCS_v2.jar'