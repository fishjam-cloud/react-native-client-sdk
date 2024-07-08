echo "Running format for react-native javascript files \n"
prettier --write . --ignore-path ./.eslintignore
echo "Running format for react-native ios files \n"
cd ios
swift-format format -i -r ./*.swift --configuration swift-format-config.json
