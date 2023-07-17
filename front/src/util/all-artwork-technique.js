function getAllArtworkTechnique(string) {
  var artworkTechniques = [
    "Painting",
    "Sculpture",
    "Collage",
    "Drawing",
    "Digital",
  ];

  if (string !== "") {
    switch (string) {
      case "PAINTING":
        return artworkTechniques[0];
      case "SCULPTURE":
        return artworkTechniques[1];
      case "COLLAGE":
        return artworkTechniques[2];
      case "DRAWING":
        return artworkTechniques[3];
      case "DIGITAL":
        return artworkTechniques[4];
      default:
        break;
    }
  }
  return artworkTechniques;
}

export default getAllArtworkTechnique;
