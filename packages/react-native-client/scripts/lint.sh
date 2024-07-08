echo "Running lint for react-native javascript files \n"
eslint . --ext .ts,.tsx --fix
echo "Running lint for react-native android files \n"
cd android
ktlint -F **/*.kt
cd ..
echo "Running lint for react-native ios files \n"
cd ios
swift-format lint -r -s ./*.swift --configuration swift-format-config.json
