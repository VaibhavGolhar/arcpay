
import { Stack } from "expo-router";
import { View, Text, ActivityIndicator } from "react-native";
import { Appbar, BottomNavigation } from "react-native-paper";
import { useFonts } from "expo-font";

function AppBar() {
  return (
    <Appbar.Header>
      <Appbar.Content title="ArcPay" 
        titleStyle={{ fontFamily: "DMSansBold", fontSize: 22 }}
      />
      <Appbar.Action icon="bell-outline" />
      <Appbar.Action icon="help-circle-outline" />
    </Appbar.Header>
  );
}



export default function RootLayout() {
  const [fontsLoaded] = useFonts({
    "DMSans": require("../assets/fonts/DMSans.ttf"),
    "DMSansBold": require("../assets/fonts/DMSansBold.ttf")
  });

  return (
    <Stack
      screenOptions={{
        header: () => <AppBar />,
        headerTitleStyle: { fontFamily: "DMSansBold", fontSize: 20 },
      }}
    />
  );
}