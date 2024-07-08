echo "Running format:check for react-native javascript files \n"
prettier --check . --ignore-path ./.eslintignore
echo "Running format:check for react-native ios files \n"
cd ios
swift-format format -r ./*.swift --configuration swift-format-config.json
