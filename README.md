# sms_prediactor
 the lowest level of language generation ever! but that's still my baby

    /**
     * Nouvelle idée
     * 
     * On est en début de phrase :
     *      l'entrée n'existe pas encore :
     *          -> ajout d'une entrée dans la Map de mot (clé = Word (occurence = 1 / Liste de mots (vide)), valeur )
     *              -> ajout du mot suivant (occurence = 1 / Liste de mots (vide) ) à la liste de mots du premier mot (EN PASSANT LA PHRASE DE PROCHE EN PROCHE ET EN SUPPRIMANT LE PREMIER MOT CHAQUE FOIS? / index)
     *                  arrêt lorsque la phrase a pour longeueur/taille 0
     *      l'entrée existe :
     *          -> On incrémente son occurence
     *              -> On cherche si le mot suivant de la phrase est dans la liste et sinon on l'ajoute à la manière du premier scénario
     *                  -> arrêt lorsque la phrase a pour longeueur/taille 0
     */ 