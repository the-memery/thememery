function getAllUserRole(input) {
  var userRoles = ["user", "artist", "admin"];

  if (input !== "" && input !== null) {
    switch (input) {
      case "ROLE_USER":
        return 0;
      case "ROLE_ARTIST":
        return 1;
      case "ROLE_ADMIN":
        return 2;
      default:
        break;
    }
  }
  return userRoles;
}

export default getAllUserRole;
