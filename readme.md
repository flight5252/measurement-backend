# README

## Miljøvariabler

For å deploye denne Spring Boot applikasjonen må du sette miljøvariabler for Travis-ci slik at Travis-ci vet hvilken Heroku app og Docker hub konto den skal deploye til og med riktig config. 


Disse miljøvariablene må settes:

* DOCKER_USERNAME
* DOCKER_PASSWORD
* HEROKU_APP_NAME

Det kan gjøres ved å bruke travis encrypt for hver enkelt slik:

```bash
 travis encrypt DOCKER_USERNAME=<verdi> --add env.global
```

```bash
 travis encrypt $(heroku auth:token) --add deploy.api_key
```


## Ekstra

* Jeg har brukt @Timed annotasjonen på 50% av endepunktene. Denne gir en del metrics ut av boksen(tid, distributionSummary m.m) ved å f.eks. gi beanen konfigurasjon
    som i mitt tilfelle er gjort med "@Timed(percentiles = [0.5, 0.95, 0.999], histogram = true)"
* Swagger dokumentasjon er tilgjengelig på /swagger-ui.html 