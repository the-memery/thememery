import React, { useContext } from "react";
import { Navigate } from "react-router-dom";
import { UserAuthContext } from "../App";

export const ProtectedRoute = ({ children }) => {
  const { currentUser, logInHandler, logOutHandler } =
    useContext(UserAuthContext);
  if (currentUser.role === 3) {
    return <Navigate to="/" />;
  }
  return children;
};
