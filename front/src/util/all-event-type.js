function getAllEventType(string) {
    // console.log(string);
    var eventTypes = [
      "exhibition",
      "pop up"
    ];
  
    if (string !== "") {
      switch (string) {
        case "TYPE_EXHIBITION":
          return 0;
        case "TYPE_POPUP":
          return 1;
        default:
          break;
      }
    }
    return eventTypes;
  }
  
  export default getAllEventType;
  