import React, { useEffect, useState, createContext } from "react";
import Home from "./pages/home";
import { Route, Routes } from "react-router-dom";
import LogIn from "./pages/logIn";
import Profile from "./pages/profile";
import Address from "./pages/address";
import { ProtectedRoute } from "./protected-route";
import Register from "./pages/register";
import ArtistProfile from "./pages/artist-profile";
import AdminPanel from "./pages/admin-panel";
import UpdateAndDeleteAddress from "./pages/update-and-delete";
import Artists from "./pages/artists";
import Ecommerce from "./pages/ecommerce";
import MyArtworks from "./pages/my-artworks";
import ShoppingCart from "./pages/shopping-cart";
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { isJwtValid } from "./services/is-auth-valid";
import Cookies from "js-cookie";

export const UserAuthContext = createContext();

const theme = createTheme();

function App() {
  const [currentUser, setCurrentUser] = useState(
    Cookies.get("currentUser") !== undefined
      ? JSON.parse(Cookies.get("currentUser"))
      : { username: "", role: 3 }
  );

  const logInHandler = (userDetails) => {
    if (userDetails === undefined) {
      return;
    }
    setCurrentUser({
      username: userDetails.username,
      // 3 = pas connecté  0 = ROLE_USER  1 = ROLE_ARTIST  2 = ROLE_ADMIN
      role:
        userDetails.role === "ROLE_USER"
          ? 0
          : userDetails.role === "ROLE_ARTIST"
            ? 1
            : 2,
    });
  };

  const logOutHandler = () => {
    Cookies.remove("jwt");
    setCurrentUser({ username: "", role: 3 });
  };

  useEffect(() => {
    checkAuth();
  }, []);

  async function checkAuth() {
    var checkAuthResponse = await isJwtValid();

    if (checkAuthResponse === false) {
      logOutHandler();
      return;
    }
  }

  useEffect(() => {
    Cookies.set("currentUser", JSON.stringify(currentUser), {
      expires: 1,
      secure: true,
    });
  }, [currentUser]);

  return (
    <ThemeProvider theme={theme}>
      <Routes>
        <Route
          path="/"
          element={
            <UserAuthContext.Provider
              value={{ currentUser, logInHandler, logOutHandler }}
            >
              <Home />
            </UserAuthContext.Provider>
          }
        />
        <Route path="/register" element={<Register />} />
        <Route
          path="/log-in"
          element={
            <UserAuthContext.Provider
              value={{ currentUser, logInHandler, logOutHandler }}
            >
              <LogIn onLogIn={logInHandler} />
            </UserAuthContext.Provider>
          }
        />
        <Route
          path="/artists"
          element={
            <UserAuthContext.Provider
              value={{ currentUser, logInHandler, logOutHandler }}
            >
              <Artists />
            </UserAuthContext.Provider>
          }
        />
        <Route
          path="/artists/:username"
          element={
            <UserAuthContext.Provider
              value={{ currentUser, logInHandler, logOutHandler }}
            >
              <ArtistProfile />
            </UserAuthContext.Provider>
          }
        />
        <Route
          path="/e-commerce"
          element={
            <UserAuthContext.Provider
              value={{ currentUser, logInHandler, logOutHandler }}
            >
              <Ecommerce />
            </UserAuthContext.Provider>
          }
        />
        <Route
          path="/profile"
          element={
            <UserAuthContext.Provider
              value={{ currentUser, logInHandler, logOutHandler }}
            >
              <ProtectedRoute>
                <Profile />
              </ProtectedRoute>
            </UserAuthContext.Provider>
          }
        />

        <Route
          path="/profile/my-artworks"
          element={
            <UserAuthContext.Provider
              value={{ currentUser, logInHandler, logOutHandler }}
            >
              <ProtectedRoute>
                <MyArtworks />
              </ProtectedRoute>
            </UserAuthContext.Provider>
          }
        />
        <Route
          path="/profile/address"
          element={
            <UserAuthContext.Provider
              value={{ currentUser, logInHandler, logOutHandler }}
            >
              <ProtectedRoute>
                <Address />
              </ProtectedRoute>
            </UserAuthContext.Provider>
          }
        />
        <Route
          path="/update"
          element={
            <UserAuthContext.Provider
              value={{ currentUser, logInHandler, logOutHandler }}
            >
              <ProtectedRoute>
                <UpdateAndDeleteAddress />
              </ProtectedRoute>
            </UserAuthContext.Provider>
          }
        />
        <Route
          path="/profile/admin"
          element={
            <UserAuthContext.Provider
              value={{ currentUser, logInHandler, logOutHandler }}
            >
              <ProtectedRoute>
                <AdminPanel />
              </ProtectedRoute>
            </UserAuthContext.Provider>
          }
        />
        <Route
          path="/shoppingcart"
          element={
            <UserAuthContext.Provider
              value={{ currentUser, logInHandler, logOutHandler }}
            >
              <ProtectedRoute>
                <ShoppingCart />
              </ProtectedRoute>
            </UserAuthContext.Provider>
          }
        />
      </Routes>
    </ThemeProvider>
  );
}

export default App;
