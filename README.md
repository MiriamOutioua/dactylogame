# Dactylogame

Dactylogame est un projet qui m'a été donné de faire pendant ma dernière année en licence informatique.
Ce projet est en quelques sorte une copie du site Monkeytype qui permet de s'entraîner à écrire au clavier.

### Comment exécuter le projet

Placez vous à la racine du projet et exécuter :

`./gradlew run`

### Le jeu

#### - Mode normal :
    
    Le mode normal vous permet de vous entraîner à écrire sans contrainte 
    contrairement au mode jeu qui sera expliqué ci-dessous. 
    Vous avez un nombre de mots prédéfini à taper (30) et à la fin de ceci vos statistiques seront données.
    
Les statistiques dont il est question sont : le nombre de mots par minute (WPM), la précision (ACC) et la régularité (REG)

#### - Mode jeu :

    Ce mode sera un challenge car il contient 3 niveaux de difficulté différents avec chacun leurs contraintes.
     À noter que dans ce mode vous aurez un nombre de vie limité et des mots bonus qui vous permette de regagner de la vie si il est tapé correctement d'un coup.
    Les mots apparaissent également avec temps régulier, 
    si il y a trop de mots présents le mot courant sera validé et vous pourrez perdre des vies !
    - Mode facile : 10 vies, taux d'apparition de bonus élevée et vitesse lente
    - Mode normal : 5 vies, taux moyen et vitesse modérée
    - Mode difficile : 3 vies. taux rare et vitesse rapide