const PolitI18n = (() => {

    const STORAGE_KEY = "polit-assistant-language";
    const DEFAULT_LANGUAGE = "de";

    const SUPPORTED_LANGUAGES = [
        "de",
        "fr",
        "it",
        "en"
    ];

    const LOCALES = {
        de: "de-CH",
        fr: "fr-CH",
        it: "it-CH",
        en: "en-GB"
    };


    const translations = {

        // =====================================================
        // DEUTSCH
        // =====================================================

        de: {

            headerSubtitle:
                "Parlamentarische Geschäfte beobachten, relevante Themen erkennen und informiert bleiben.",

            monitoring:
                "WWF Polit-Monitoring",

            missNothing:
                "Nichts Relevantes verpassen",

            subscriptionDescription:
                "Abonnieren Sie Ihre WWF-Themen und erhalten Sie automatisch Hinweise zu neuen relevanten Geschäften und bevorstehenden Traktanden.",

            subscriptionNote:
                "Bleiben Sie informiert, wenn sich im Parlament etwas zu Ihren Themen bewegt – von neuen Geschäften bis zu relevanten Sessionstraktanden.",

            subscribeTopics:
                "Themen abonnieren",

            manageSubscription:
                "Bestehendes Abonnement verwalten",

            subscribeMyTopics:
                "Meine WWF-Themen abonnieren",

            selectTopics:
                "Wählen Sie die Themen aus, über die Sie automatisch informiert werden möchten.",

            name:
                "Name",

            email:
                "E-Mail",

            wwfTopics:
                "WWF-Themen",

            subscribeNow:
                "Jetzt informieren lassen",

            back:
                "Zurück",

            manageTitle:
                "Abonnement verwalten",

            manageDescription:
                "Geben Sie Ihre E-Mail-Adresse ein. Wir senden Ihnen einen sicheren Link zur Verwaltung Ihres Abonnements.",

            sendManagementLink:
                "Verwaltungslink senden",

            emailSent:
                "E-Mail gesendet",

            checkInbox:
                "Bitte prüfen Sie Ihr Postfach.",

            subscriptionLoading:
                "Abonnement wird geladen...",

            subscriptionActivating:
                "Abonnement wird aktiviert...",
            subscriptionAlreadyActive:
                "Für diese E-Mail-Adresse besteht bereits ein aktives Abonnement. Bitte verwalten Sie Ihre Einstellungen über «Bestehendes Abonnement verwalten».",

            pleaseWait:
                "Bitte warten Sie einen Moment.",

            emailConfirmed:
                "E-Mail-Adresse bestätigt",

            subscriptionActive:
                "Abonnement aktiv",

            hello:
                "Guten Tag",

            registeredWith:
                "Sie sind mit",

            registeredTopics:
                "für folgende WWF-Themen angemeldet:",

            activeSubscriptionNote:
                "Wir informieren Sie per E-Mail über neue relevante parlamentarische Geschäfte und bevorstehende Traktanden zu Ihren Themen.",

            updateTopics:
                "Themen aktualisieren",

            deleteSubscription:
                "Abonnement löschen",

            continueTopics:
                "Wählen Sie die WWF-Themen aus, über die Sie weiterhin informiert werden möchten.",

            saveChanges:
                "Änderungen speichern",

            cancel:
                "Abbrechen",

            successful:
                "Erfolgreich",

            toOverview:
                "Zur Übersicht",

            affairsKicker:
                "Polit-Monitoring",

            affairsTitle:
                "Parlamentarische Geschäfte",

            affairsDescription:
                "Durchsuchen Sie parlamentarische Geschäfte und filtern Sie nach WWF-Themen.",

            searchTerm:
                "Suchbegriff",

            searchPlaceholder:
                "z. B. Gewässer, Solarenergie, Pestizide",

            wwfTopic:
                "WWF-Thema",

            allTopics:
                "Alle Themen",

            search:
                "Suchen",

            affairsLoading:
                "Geschäfte werden geladen...",

            affairsLoadError:
                "Geschäfte konnten nicht geladen werden.",

            noAffairs:
                "Keine passenden Geschäfte gefunden.",

            affairsOnPage:
                "{count} Geschäft(e) auf dieser Seite",

            page:
                "Seite {page}",

            previous:
                "Zurück",

            next:
                "Weiter",

            details:
                "Details anzeigen →",

            affairFallback:
                "Geschäft {id}",

            upcoming:
                "Demnächst",

            upcomingAgendas:
                "Bevorstehende relevante Traktanden",

            upcomingDescription:
                "Die nächsten Sessionstraktanden mit Bezug zu WWF-Themen.",

            agendasLoading:
                "Traktanden werden geladen...",

            agendasLoadError:
                "Traktanden konnten nicht geladen werden.",

            noAgendas:
                "Keine relevanten Traktanden in den kommenden 30 Tagen gefunden.",

            upcomingAgendaCount:
                "{count} bevorstehende Traktanden",

            agenda:
                "Traktandum",

            agendaWithNumber:
                "Traktandum {number}",

            viewAffair:
                "Geschäft ansehen →",

            new:
                "Neu",

            newMonitoring:
                "Neu im Polit-Monitoring",

            newDescription:
                "Die fünf neuesten parlamentarischen Geschäfte im System.",

            newLoading:
                "Neue Geschäfte werden geladen...",

            newLoadError:
                "Neue Geschäfte konnten nicht geladen werden.",

            noNewAffairs:
                "Keine neuen relevanten Geschäfte gefunden.",

            newAffairCount:
                "{count} neue relevante Geschäfte",

            data:
                "Daten",

            dataDescription:
                "Der Polit-Assistant verbindet parlamentarische Open Data mit einer automatischen WWF-Themenklassifikation.",

            dataSource:
                "Datenquelle",

            eightTopics:
                "8 Themen",

            dataUpdate:
                "Datenaktualisierung",

            automated:
                "Automatisiert",

            notifications:
                "Benachrichtigungen",

            footerSource:
                "Datenquelle: OpenParlData.ch",

            genericError:
                "Ein Fehler ist aufgetreten.",

            selectAtLeastOne:
                "Bitte mindestens ein WWF-Thema auswählen.",

            verificationFailed:
                "Das Abonnement konnte nicht aktiviert werden.",

            subscriptionLoadFailed:
                "Das Abonnement konnte nicht geladen werden.",

            subscriptionRequestFailed:
                "Das Abonnement konnte nicht angefordert werden.",

            requestFailed:
                "Die Anfrage konnte nicht verarbeitet werden.",

            managementLinkLoading:
                "Sicherer Verwaltungslink wird angefordert...",

            managementLinkFailed:
                "Der Verwaltungslink konnte nicht angefordert werden.",

            managementLinkRequired:
                "Für diese Änderung wird ein gültiger Verwaltungslink benötigt.",

            updateFailed:
                "Das Abonnement konnte nicht aktualisiert werden.",

            deleteFailed:
                "Das Abonnement konnte nicht gelöscht werden.",

            subscriptionMailSent:
                "Bitte prüfen Sie Ihr Postfach. Sie erhalten eine E-Mail mit den nächsten Schritten.",

            noActiveSubscription:
                "Für diese E-Mail-Adresse besteht kein aktives Abonnement.",

            managementMailSent:
                "Der Verwaltungslink wurde per E-Mail gesendet.",

            secureManagementMailSent:
                "Zum Schutz Ihres Abonnements haben wir Ihnen einen sicheren Verwaltungslink per E-Mail gesendet.",

            subscriptionUpdated:
                "Abonnement aktualisiert",

            subscriptionUpdatedText:
                "Ihre WWF-Themen wurden erfolgreich aktualisiert. Eine Bestätigung wurde an Ihre E-Mail-Adresse gesendet.",

            subscriptionEnded:
                "Abonnement beendet",

            subscriptionEndedText:
                "Ihr Abonnement wurde erfolgreich beendet. Eine Bestätigung wurde an Ihre E-Mail-Adresse gesendet.",

            deleteConfirmation:
                "Möchten Sie Ihr WWF-Themen-Abonnement wirklich löschen? Sie erhalten danach keine weiteren Benachrichtigungen.",


            // =================================================
            // AFFAIR DETAIL
            // =================================================

            affairDetailKicker:
                "Parlamentarisches Geschäft",

            affairDetailLoading:
                "Geschäft wird geladen...",

            affairDetailLoadError:
                "Das Geschäft konnte nicht geladen werden.",

            affairNotFound:
                "Das parlamentarische Geschäft wurde nicht gefunden.",

            backOverview:
                "Zurück zur Übersicht",

            content:
                "Inhalt",

            affairContent:
                "Inhalt des Geschäfts",

            affairContentDescription:
                "Inhalt aus den bereits importierten parlamentarischen Dokumenten.",

            noAffairContent:
                "Für dieses Geschäft ist noch kein Dokumentinhalt verfügbar.",

            readMore:
                "Weiterlesen ↓",

            showLess:
                "Weniger anzeigen ↑",

            wwfRelevance:
                "WWF-Relevanz",

            relevantTopics:
                "Für dieses Geschäft erkannte relevante Themen.",

            noRelevantTopics:
                "Für dieses Geschäft wurden keine WWF-Themen erkannt.",

            documents:
                "Dokumente",

            relatedDocuments:
                "Zugehörige Dokumente",

            documentsDescription:
                "Bereits importierte Dokumente zu diesem Geschäft.",

            noDocuments:
                "Für dieses Geschäft sind keine Dokumente verfügbar.",

            originalSource:
                "Originalquelle öffnen ↗",

            originalDocument:
                "Originaldokument öffnen ↗",

            document:
                "Dokument",

            documentLanguage:
                "Sprache",

            documentFormat:
                "Format",

            documentDate:
                "Datum",

            number:
                "Nummer",

            type:
                "Typ",

            state:
                "Status",

            beginDate:
                "Beginn",

            endDate:
                "Ende",

            topics: {
                "Energie": "Energie",
                "ENERGIE": "Energie",

                "Biodiversität": "Biodiversität",
                "Biodiversitaet": "Biodiversität",
                "BIODIVERSITAET": "Biodiversität",

                "Wasser": "Wasser",
                "WASSER": "Wasser",

                "Landwirtschaft": "Landwirtschaft",
                "LANDWIRTSCHAFT": "Landwirtschaft",

                "Raumplanung": "Raumplanung",
                "RAUMPLANUNG": "Raumplanung",

                "Klima": "Klima",
                "KLIMA": "Klima",

                "Mobilität": "Mobilität",
                "Mobilitaet": "Mobilität",
                "MOBILITAET": "Mobilität",

                "Abfall": "Abfall",
                "ABFALL": "Abfall",

                "Sonstiges": "Sonstiges",
                "SONSTIGES": "Sonstiges"
            }
        },


        // =====================================================
        // FRANÇAIS
        // =====================================================

        fr: {

            headerSubtitle:
                "Suivre les objets parlementaires, identifier les thèmes pertinents et rester informé.",

            monitoring:
                "Suivi politique WWF",

            missNothing:
                "Ne manquez rien d’important",

            subscriptionDescription:
                "Abonnez-vous à vos thèmes WWF et recevez automatiquement des informations sur les nouveaux objets pertinents et les prochains points à l’ordre du jour.",

            subscriptionNote:
                "Restez informé des évolutions parlementaires liées à vos thèmes, des nouveaux objets aux points de session pertinents.",

            subscribeTopics:
                "S’abonner aux thèmes",

            manageSubscription:
                "Gérer un abonnement existant",

            subscribeMyTopics:
                "S’abonner à mes thèmes WWF",

            selectTopics:
                "Sélectionnez les thèmes pour lesquels vous souhaitez recevoir automatiquement des informations.",

            name:
                "Nom",

            email:
                "E-mail",

            wwfTopics:
                "Thèmes WWF",

            subscribeNow:
                "M’informer",

            back:
                "Retour",

            manageTitle:
                "Gérer l’abonnement",

            manageDescription:
                "Saisissez votre adresse e-mail. Nous vous enverrons un lien sécurisé pour gérer votre abonnement.",

            sendManagementLink:
                "Envoyer le lien de gestion",

            emailSent:
                "E-mail envoyé",

            checkInbox:
                "Veuillez consulter votre boîte de réception.",

            subscriptionLoading:
                "Chargement de l’abonnement...",

            subscriptionActivating:
                "Activation de l’abonnement...",

            subscriptionAlreadyActive:
                "Un abonnement actif existe déjà pour cette adresse e-mail. Veuillez gérer vos paramètres via «Gérer un abonnement existant».",

            pleaseWait:
                "Veuillez patienter un instant.",

            emailConfirmed:
                "Adresse e-mail confirmée",

            subscriptionActive:
                "Abonnement actif",

            hello:
                "Bonjour",

            registeredWith:
                "Vous êtes inscrit avec",

            registeredTopics:
                "pour les thèmes WWF suivants :",

            activeSubscriptionNote:
                "Nous vous informons par e-mail des nouveaux objets parlementaires pertinents et des prochains points à l’ordre du jour liés à vos thèmes.",

            updateTopics:
                "Modifier les thèmes",

            deleteSubscription:
                "Supprimer l’abonnement",

            continueTopics:
                "Sélectionnez les thèmes WWF pour lesquels vous souhaitez continuer à recevoir des informations.",

            saveChanges:
                "Enregistrer les modifications",

            cancel:
                "Annuler",

            successful:
                "Terminé",

            toOverview:
                "Retour à l’aperçu",

            affairsKicker:
                "Suivi politique",

            affairsTitle:
                "Objets parlementaires",

            affairsDescription:
                "Recherchez des objets parlementaires et filtrez-les par thème WWF.",

            searchTerm:
                "Terme de recherche",

            searchPlaceholder:
                "p. ex. eaux, énergie solaire, pesticides",

            wwfTopic:
                "Thème WWF",

            allTopics:
                "Tous les thèmes",

            search:
                "Rechercher",

            affairsLoading:
                "Chargement des objets...",

            affairsLoadError:
                "Impossible de charger les objets.",

            noAffairs:
                "Aucun objet correspondant trouvé.",

            affairsOnPage:
                "{count} objet(s) sur cette page",

            page:
                "Page {page}",

            previous:
                "Précédent",

            next:
                "Suivant",

            details:
                "Afficher les détails →",

            affairFallback:
                "Objet {id}",

            upcoming:
                "Prochainement",

            upcomingAgendas:
                "Prochains points pertinents",

            upcomingDescription:
                "Les prochains points de session liés aux thèmes WWF.",

            agendasLoading:
                "Chargement des points...",

            agendasLoadError:
                "Impossible de charger les points.",

            noAgendas:
                "Aucun point pertinent trouvé pour les 30 prochains jours.",

            upcomingAgendaCount:
                "{count} point(s) à venir",

            agenda:
                "Point",

            agendaWithNumber:
                "Point {number}",

            viewAffair:
                "Voir l’objet →",

            new:
                "Nouveau",

            newMonitoring:
                "Nouveau dans le suivi politique",

            newDescription:
                "Les cinq objets parlementaires les plus récents du système.",

            newLoading:
                "Chargement des nouveaux objets...",

            newLoadError:
                "Impossible de charger les nouveaux objets.",

            noNewAffairs:
                "Aucun nouvel objet pertinent trouvé.",

            newAffairCount:
                "{count} nouvel/nouveaux objet(s) pertinent(s)",

            data:
                "Données",

            dataDescription:
                "Le Polit-Assistant combine les données parlementaires ouvertes avec une classification automatique des thèmes WWF.",

            dataSource:
                "Source des données",

            eightTopics:
                "8 thèmes",

            dataUpdate:
                "Mise à jour des données",

            automated:
                "Automatisée",

            notifications:
                "Notifications",

            footerSource:
                "Source des données : OpenParlData.ch",

            genericError:
                "Une erreur est survenue.",

            selectAtLeastOne:
                "Veuillez sélectionner au moins un thème WWF.",

            verificationFailed:
                "L’abonnement n’a pas pu être activé.",

            subscriptionLoadFailed:
                "L’abonnement n’a pas pu être chargé.",

            subscriptionRequestFailed:
                "La demande d’abonnement n’a pas pu être traitée.",

            requestFailed:
                "La demande n’a pas pu être traitée.",

            managementLinkLoading:
                "Demande d’un lien de gestion sécurisé...",

            managementLinkFailed:
                "Le lien de gestion n’a pas pu être demandé.",

            managementLinkRequired:
                "Un lien de gestion valide est nécessaire pour cette modification.",

            updateFailed:
                "L’abonnement n’a pas pu être mis à jour.",

            deleteFailed:
                "L’abonnement n’a pas pu être supprimé.",

            subscriptionMailSent:
                "Veuillez consulter votre boîte de réception. Vous recevrez un e-mail avec les prochaines étapes.",

            noActiveSubscription:
                "Aucun abonnement actif n’existe pour cette adresse e-mail.",

            managementMailSent:
                "Le lien de gestion a été envoyé par e-mail.",

            secureManagementMailSent:
                "Pour protéger votre abonnement, nous vous avons envoyé un lien de gestion sécurisé par e-mail.",

            subscriptionUpdated:
                "Abonnement mis à jour",

            subscriptionUpdatedText:
                "Vos thèmes WWF ont été mis à jour avec succès. Une confirmation a été envoyée à votre adresse e-mail.",

            subscriptionEnded:
                "Abonnement terminé",

            subscriptionEndedText:
                "Votre abonnement a été résilié avec succès. Une confirmation a été envoyée à votre adresse e-mail.",

            deleteConfirmation:
                "Voulez-vous vraiment supprimer votre abonnement aux thèmes WWF ? Vous ne recevrez ensuite plus de notifications.",


            // =================================================
            // AFFAIR DETAIL
            // =================================================

            affairDetailKicker:
                "Objet parlementaire",

            affairDetailLoading:
                "Chargement de l’objet...",

            affairDetailLoadError:
                "Impossible de charger l’objet.",

            affairNotFound:
                "L’objet parlementaire n’a pas été trouvé.",

            backOverview:
                "Retour à l’aperçu",

            content:
                "Contenu",

            affairContent:
                "Contenu de l’objet",

            affairContentDescription:
                "Contenu provenant des documents parlementaires déjà importés.",

            noAffairContent:
                "Aucun contenu de document n’est encore disponible pour cet objet.",

            readMore:
                "Lire la suite ↓",

            showLess:
                "Afficher moins ↑",

            wwfRelevance:
                "Pertinence WWF",

            relevantTopics:
                "Thèmes pertinents identifiés pour cet objet.",

            noRelevantTopics:
                "Aucun thème WWF n’a été identifié pour cet objet.",

            documents:
                "Documents",

            relatedDocuments:
                "Documents associés",

            documentsDescription:
                "Documents déjà importés pour cet objet.",

            noDocuments:
                "Aucun document n’est disponible pour cet objet.",

            originalSource:
                "Ouvrir la source originale ↗",

            originalDocument:
                "Ouvrir le document original ↗",

            document:
                "Document",

            documentLanguage:
                "Langue",

            documentFormat:
                "Format",

            documentDate:
                "Date",

            number:
                "Numéro",

            type:
                "Type",

            state:
                "Statut",

            beginDate:
                "Début",

            endDate:
                "Fin",

            topics: {
                "Energie": "Énergie",
                "ENERGIE": "Énergie",

                "Biodiversität": "Biodiversité",
                "Biodiversitaet": "Biodiversité",
                "BIODIVERSITAET": "Biodiversité",

                "Wasser": "Eau",
                "WASSER": "Eau",

                "Landwirtschaft": "Agriculture",
                "LANDWIRTSCHAFT": "Agriculture",

                "Raumplanung": "Aménagement du territoire",
                "RAUMPLANUNG": "Aménagement du territoire",

                "Klima": "Climat",
                "KLIMA": "Climat",

                "Mobilität": "Mobilité",
                "Mobilitaet": "Mobilité",
                "MOBILITAET": "Mobilité",

                "Abfall": "Déchets",
                "ABFALL": "Déchets",

                "Sonstiges": "Autres",
                "SONSTIGES": "Autres"
            }
        },


        // =====================================================
        // ITALIANO
        // =====================================================

        it: {

            headerSubtitle:
                "Monitorare gli affari parlamentari, riconoscere i temi rilevanti e rimanere informati.",

            monitoring:
                "Monitoraggio politico WWF",

            missNothing:
                "Non perdere nulla di importante",

            subscriptionDescription:
                "Abbonati ai tuoi temi WWF e ricevi automaticamente informazioni sui nuovi affari rilevanti e sui prossimi punti all’ordine del giorno.",

            subscriptionNote:
                "Rimani informato sugli sviluppi parlamentari relativi ai tuoi temi, dai nuovi affari ai punti di sessione rilevanti.",

            subscribeTopics:
                "Abbonati ai temi",

            manageSubscription:
                "Gestisci un abbonamento esistente",

            subscribeMyTopics:
                "Abbonati ai miei temi WWF",

            selectTopics:
                "Seleziona i temi sui quali desideri ricevere automaticamente informazioni.",

            name:
                "Nome",

            email:
                "E-mail",

            wwfTopics:
                "Temi WWF",

            subscribeNow:
                "Tienimi informato",

            back:
                "Indietro",

            manageTitle:
                "Gestisci abbonamento",

            manageDescription:
                "Inserisci il tuo indirizzo e-mail. Ti invieremo un link sicuro per gestire il tuo abbonamento.",

            sendManagementLink:
                "Invia link di gestione",

            emailSent:
                "E-mail inviata",

            checkInbox:
                "Controlla la tua casella di posta.",

            subscriptionLoading:
                "Caricamento dell’abbonamento...",

            subscriptionActivating:
                "Attivazione dell’abbonamento...",

            subscriptionAlreadyActive:
                "Esiste già un abbonamento attivo per questo indirizzo e-mail. Gestisci le impostazioni tramite «Gestisci un abbonamento esistente».",

            pleaseWait:
                "Attendi un momento.",

            emailConfirmed:
                "Indirizzo e-mail confermato",

            subscriptionActive:
                "Abbonamento attivo",

            hello:
                "Buongiorno",

            registeredWith:
                "Sei registrato con",

            registeredTopics:
                "per i seguenti temi WWF:",

            activeSubscriptionNote:
                "Ti informiamo via e-mail sui nuovi affari parlamentari rilevanti e sui prossimi punti all’ordine del giorno relativi ai tuoi temi.",

            updateTopics:
                "Aggiorna temi",

            deleteSubscription:
                "Elimina abbonamento",

            continueTopics:
                "Seleziona i temi WWF sui quali desideri continuare a ricevere informazioni.",

            saveChanges:
                "Salva modifiche",

            cancel:
                "Annulla",

            successful:
                "Operazione riuscita",

            toOverview:
                "Torna alla panoramica",

            affairsKicker:
                "Monitoraggio politico",

            affairsTitle:
                "Affari parlamentari",

            affairsDescription:
                "Cerca gli affari parlamentari e filtrali per tema WWF.",

            searchTerm:
                "Termine di ricerca",

            searchPlaceholder:
                "ad es. acque, energia solare, pesticidi",

            wwfTopic:
                "Tema WWF",

            allTopics:
                "Tutti i temi",

            search:
                "Cerca",

            affairsLoading:
                "Caricamento degli affari...",

            affairsLoadError:
                "Impossibile caricare gli affari.",

            noAffairs:
                "Nessun affare corrispondente trovato.",

            affairsOnPage:
                "{count} affare/i su questa pagina",

            page:
                "Pagina {page}",

            previous:
                "Indietro",

            next:
                "Avanti",

            details:
                "Mostra dettagli →",

            affairFallback:
                "Affare {id}",

            upcoming:
                "Prossimamente",

            upcomingAgendas:
                "Prossimi punti rilevanti",

            upcomingDescription:
                "I prossimi punti di sessione relativi ai temi WWF.",

            agendasLoading:
                "Caricamento dei punti...",

            agendasLoadError:
                "Impossibile caricare i punti.",

            noAgendas:
                "Nessun punto rilevante trovato nei prossimi 30 giorni.",

            upcomingAgendaCount:
                "{count} punto/i imminente/i",

            agenda:
                "Punto",

            agendaWithNumber:
                "Punto {number}",

            viewAffair:
                "Visualizza affare →",

            new:
                "Nuovo",

            newMonitoring:
                "Novità nel monitoraggio politico",

            newDescription:
                "I cinque affari parlamentari più recenti nel sistema.",

            newLoading:
                "Caricamento dei nuovi affari...",

            newLoadError:
                "Impossibile caricare i nuovi affari.",

            noNewAffairs:
                "Nessun nuovo affare rilevante trovato.",

            newAffairCount:
                "{count} nuovo/i affare/i rilevante/i",

            data:
                "Dati",

            dataDescription:
                "Il Polit-Assistant combina gli open data parlamentari con una classificazione automatica dei temi WWF.",

            dataSource:
                "Fonte dei dati",

            eightTopics:
                "8 temi",

            dataUpdate:
                "Aggiornamento dati",

            automated:
                "Automatico",

            notifications:
                "Notifiche",

            footerSource:
                "Fonte dei dati: OpenParlData.ch",

            genericError:
                "Si è verificato un errore.",

            selectAtLeastOne:
                "Seleziona almeno un tema WWF.",

            verificationFailed:
                "Non è stato possibile attivare l’abbonamento.",

            subscriptionLoadFailed:
                "Non è stato possibile caricare l’abbonamento.",

            subscriptionRequestFailed:
                "Non è stato possibile richiedere l’abbonamento.",

            requestFailed:
                "Non è stato possibile elaborare la richiesta.",

            managementLinkLoading:
                "Richiesta di un link di gestione sicuro...",

            managementLinkFailed:
                "Non è stato possibile richiedere il link di gestione.",

            managementLinkRequired:
                "Per questa modifica è necessario un link di gestione valido.",

            updateFailed:
                "Non è stato possibile aggiornare l’abbonamento.",

            deleteFailed:
                "Non è stato possibile eliminare l’abbonamento.",

            subscriptionMailSent:
                "Controlla la tua casella di posta. Riceverai un’e-mail con i prossimi passaggi.",

            noActiveSubscription:
                "Non esiste alcun abbonamento attivo per questo indirizzo e-mail.",

            managementMailSent:
                "Il link di gestione è stato inviato via e-mail.",

            secureManagementMailSent:
                "Per proteggere il tuo abbonamento, ti abbiamo inviato un link di gestione sicuro via e-mail.",

            subscriptionUpdated:
                "Abbonamento aggiornato",

            subscriptionUpdatedText:
                "I tuoi temi WWF sono stati aggiornati con successo. Una conferma è stata inviata al tuo indirizzo e-mail.",

            subscriptionEnded:
                "Abbonamento terminato",

            subscriptionEndedText:
                "Il tuo abbonamento è stato terminato con successo. Una conferma è stata inviata al tuo indirizzo e-mail.",

            deleteConfirmation:
                "Vuoi davvero eliminare il tuo abbonamento ai temi WWF? In seguito non riceverai più notifiche.",


            // =================================================
            // AFFAIR DETAIL
            // =================================================

            affairDetailKicker:
                "Affare parlamentare",

            affairDetailLoading:
                "Caricamento dell’affare...",

            affairDetailLoadError:
                "Impossibile caricare l’affare.",

            affairNotFound:
                "L’affare parlamentare non è stato trovato.",

            backOverview:
                "Torna alla panoramica",

            content:
                "Contenuto",

            affairContent:
                "Contenuto dell’affare",

            affairContentDescription:
                "Contenuto proveniente dai documenti parlamentari già importati.",

            noAffairContent:
                "Non è ancora disponibile alcun contenuto documentale per questo affare.",

            readMore:
                "Continua a leggere ↓",

            showLess:
                "Mostra meno ↑",

            wwfRelevance:
                "Rilevanza WWF",

            relevantTopics:
                "Temi rilevanti identificati per questo affare.",

            noRelevantTopics:
                "Nessun tema WWF è stato identificato per questo affare.",

            documents:
                "Documenti",

            relatedDocuments:
                "Documenti associati",

            documentsDescription:
                "Documenti già importati per questo affare.",

            noDocuments:
                "Non sono disponibili documenti per questo affare.",

            originalSource:
                "Apri fonte originale ↗",

            originalDocument:
                "Apri documento originale ↗",

            document:
                "Documento",

            documentLanguage:
                "Lingua",

            documentFormat:
                "Formato",

            documentDate:
                "Data",

            number:
                "Numero",

            type:
                "Tipo",

            state:
                "Stato",

            beginDate:
                "Inizio",

            endDate:
                "Fine",

            topics: {
                "Energie": "Energia",
                "ENERGIE": "Energia",

                "Biodiversität": "Biodiversità",
                "Biodiversitaet": "Biodiversità",
                "BIODIVERSITAET": "Biodiversità",

                "Wasser": "Acqua",
                "WASSER": "Acqua",

                "Landwirtschaft": "Agricoltura",
                "LANDWIRTSCHAFT": "Agricoltura",

                "Raumplanung": "Pianificazione territoriale",
                "RAUMPLANUNG": "Pianificazione territoriale",

                "Klima": "Clima",
                "KLIMA": "Clima",

                "Mobilität": "Mobilità",
                "Mobilitaet": "Mobilità",
                "MOBILITAET": "Mobilità",

                "Abfall": "Rifiuti",
                "ABFALL": "Rifiuti",

                "Sonstiges": "Altro",
                "SONSTIGES": "Altro"
            }
        },


        // =====================================================
        // ENGLISH
        // =====================================================

        en: {

            headerSubtitle:
                "Monitor parliamentary affairs, identify relevant topics and stay informed.",

            monitoring:
                "WWF Political Monitoring",

            missNothing:
                "Don't miss anything relevant",

            subscriptionDescription:
                "Subscribe to your WWF topics and automatically receive updates about new relevant affairs and upcoming agenda items.",

            subscriptionNote:
                "Stay informed when parliamentary developments affect your topics – from new affairs to relevant session agenda items.",

            subscribeTopics:
                "Subscribe to topics",

            manageSubscription:
                "Manage existing subscription",

            subscribeMyTopics:
                "Subscribe to my WWF topics",

            selectTopics:
                "Select the topics you would like to receive automatic updates about.",

            name:
                "Name",

            email:
                "Email",

            wwfTopics:
                "WWF topics",

            subscribeNow:
                "Keep me informed",

            back:
                "Back",

            manageTitle:
                "Manage subscription",

            manageDescription:
                "Enter your email address. We will send you a secure link to manage your subscription.",

            sendManagementLink:
                "Send management link",

            emailSent:
                "Email sent",

            checkInbox:
                "Please check your inbox.",

            subscriptionLoading:
                "Loading subscription...",

            subscriptionActivating:
                "Activating subscription...",

            subscriptionAlreadyActive:
                "An active subscription already exists for this email address. Please manage your settings via “Manage existing subscription”.",

            pleaseWait:
                "Please wait a moment.",

            emailConfirmed:
                "Email address confirmed",

            subscriptionActive:
                "Subscription active",

            hello:
                "Hello",

            registeredWith:
                "You are registered with",

            registeredTopics:
                "for the following WWF topics:",

            activeSubscriptionNote:
                "We will notify you by email about new relevant parliamentary affairs and upcoming agenda items related to your topics.",

            updateTopics:
                "Update topics",

            deleteSubscription:
                "Delete subscription",

            continueTopics:
                "Select the WWF topics you would like to continue receiving updates about.",

            saveChanges:
                "Save changes",

            cancel:
                "Cancel",

            successful:
                "Successful",

            toOverview:
                "Back to overview",

            affairsKicker:
                "Political Monitoring",

            affairsTitle:
                "Parliamentary affairs",

            affairsDescription:
                "Search parliamentary affairs and filter them by WWF topic.",

            searchTerm:
                "Search term",

            searchPlaceholder:
                "e.g. water, solar energy, pesticides",

            wwfTopic:
                "WWF topic",

            allTopics:
                "All topics",

            search:
                "Search",

            affairsLoading:
                "Loading affairs...",

            affairsLoadError:
                "Affairs could not be loaded.",

            noAffairs:
                "No matching affairs found.",

            affairsOnPage:
                "{count} affair(s) on this page",

            page:
                "Page {page}",

            previous:
                "Previous",

            next:
                "Next",

            details:
                "View details →",

            affairFallback:
                "Affair {id}",

            upcoming:
                "Upcoming",

            upcomingAgendas:
                "Upcoming relevant agenda items",

            upcomingDescription:
                "The next session agenda items related to WWF topics.",

            agendasLoading:
                "Loading agenda items...",

            agendasLoadError:
                "Agenda items could not be loaded.",

            noAgendas:
                "No relevant agenda items found in the next 30 days.",

            upcomingAgendaCount:
                "{count} upcoming agenda item(s)",

            agenda:
                "Agenda item",

            agendaWithNumber:
                "Agenda item {number}",

            viewAffair:
                "View affair →",

            new:
                "New",

            newMonitoring:
                "New in political monitoring",

            newDescription:
                "The five most recent parliamentary affairs in the system.",

            newLoading:
                "Loading new affairs...",

            newLoadError:
                "New affairs could not be loaded.",

            noNewAffairs:
                "No new relevant affairs found.",

            newAffairCount:
                "{count} new relevant affair(s)",

            data:
                "Data",

            dataDescription:
                "The Polit-Assistant combines parliamentary open data with automatic WWF topic classification.",

            dataSource:
                "Data source",

            eightTopics:
                "8 topics",

            dataUpdate:
                "Data update",

            automated:
                "Automated",

            notifications:
                "Notifications",

            footerSource:
                "Data source: OpenParlData.ch",

            genericError:
                "An error occurred.",

            selectAtLeastOne:
                "Please select at least one WWF topic.",

            verificationFailed:
                "The subscription could not be activated.",

            subscriptionLoadFailed:
                "The subscription could not be loaded.",

            subscriptionRequestFailed:
                "The subscription request could not be processed.",

            requestFailed:
                "The request could not be processed.",

            managementLinkLoading:
                "Requesting a secure management link...",

            managementLinkFailed:
                "The management link could not be requested.",

            managementLinkRequired:
                "A valid management link is required for this change.",

            updateFailed:
                "The subscription could not be updated.",

            deleteFailed:
                "The subscription could not be deleted.",

            subscriptionMailSent:
                "Please check your inbox. You will receive an email with the next steps.",

            noActiveSubscription:
                "There is no active subscription for this email address.",

            managementMailSent:
                "The management link has been sent by email.",

            secureManagementMailSent:
                "To protect your subscription, we sent you a secure management link by email.",

            subscriptionUpdated:
                "Subscription updated",

            subscriptionUpdatedText:
                "Your WWF topics were updated successfully. A confirmation was sent to your email address.",

            subscriptionEnded:
                "Subscription ended",

            subscriptionEndedText:
                "Your subscription was ended successfully. A confirmation was sent to your email address.",

            deleteConfirmation:
                "Do you really want to delete your WWF topic subscription? You will no longer receive notifications afterwards.",


            // =================================================
            // AFFAIR DETAIL
            // =================================================

            affairDetailKicker:
                "Parliamentary affair",

            affairDetailLoading:
                "Loading affair...",

            affairDetailLoadError:
                "The affair could not be loaded.",

            affairNotFound:
                "The parliamentary affair was not found.",

            backOverview:
                "Back to overview",

            content:
                "Content",

            affairContent:
                "Content of the affair",

            affairContentDescription:
                "Content from the parliamentary documents already imported into the system.",

            noAffairContent:
                "No document content is currently available for this affair.",

            readMore:
                "Read more ↓",

            showLess:
                "Show less ↑",

            wwfRelevance:
                "WWF relevance",

            relevantTopics:
                "Relevant topics identified for this affair.",

            noRelevantTopics:
                "No WWF topics were identified for this affair.",

            documents:
                "Documents",

            relatedDocuments:
                "Related documents",

            documentsDescription:
                "Documents already imported for this affair.",

            noDocuments:
                "No documents are available for this affair.",

            originalSource:
                "Open original source ↗",

            originalDocument:
                "Open original document ↗",

            document:
                "Document",

            documentLanguage:
                "Language",

            documentFormat:
                "Format",

            documentDate:
                "Date",

            number:
                "Number",

            type:
                "Type",

            state:
                "Status",

            beginDate:
                "Start",

            endDate:
                "End",

            topics: {
                "Energie": "Energy",
                "ENERGIE": "Energy",

                "Biodiversität": "Biodiversity",
                "Biodiversitaet": "Biodiversity",
                "BIODIVERSITAET": "Biodiversity",

                "Wasser": "Water",
                "WASSER": "Water",

                "Landwirtschaft": "Agriculture",
                "LANDWIRTSCHAFT": "Agriculture",

                "Raumplanung": "Spatial planning",
                "RAUMPLANUNG": "Spatial planning",

                "Klima": "Climate",
                "KLIMA": "Climate",

                "Mobilität": "Mobility",
                "Mobilitaet": "Mobility",
                "MOBILITAET": "Mobility",

                "Abfall": "Waste",
                "ABFALL": "Waste",

                "Sonstiges": "Other",
                "SONSTIGES": "Other"
            }
        }
    };


    // =========================================================
    // LANGUAGE
    // =========================================================

    function getLanguage() {

        try {

            const stored =
                localStorage.getItem(
                    STORAGE_KEY
                );

            if (
                SUPPORTED_LANGUAGES.includes(
                    stored
                )
            ) {
                return stored;
            }

        } catch (error) {

            console.warn(
                "Language preference could not be read.",
                error
            );
        }

        return DEFAULT_LANGUAGE;
    }


    function setLanguage(language) {

        if (
            !SUPPORTED_LANGUAGES.includes(
                language
            )
        ) {
            return;
        }

        try {

            localStorage.setItem(
                STORAGE_KEY,
                language
            );

        } catch (error) {

            console.warn(
                "Language preference could not be saved.",
                error
            );
        }

        document.documentElement.lang =
            language;

        window.dispatchEvent(
            new CustomEvent(
                "polit-language-change",
                {
                    detail: {
                        language
                    }
                }
            )
        );
    }


    // =========================================================
    // TRANSLATION
    // =========================================================

    function t(
        key,
        parameters = {}
    ) {

        const language =
            getLanguage();

        let value =
            translations[language]?.[key];

        if (
            value === undefined ||
            value === null
        ) {

            value =
                translations.de?.[key];
        }

        if (
            value === undefined ||
            value === null
        ) {

            return key;
        }

        if (
            typeof value !==
            "string"
        ) {

            return value;
        }

        return interpolate(
            value,
            parameters
        );
    }


    function interpolate(
        value,
        parameters
    ) {

        let result =
            value;

        for (
            const [key, parameter]
            of Object.entries(
            parameters
        )
            ) {

            result =
                result.replaceAll(
                    `{${key}}`,
                    String(parameter)
                );
        }

        return result;
    }


    // =========================================================
    // TOPICS
    // =========================================================

    function topic(value) {

        if (!value) {
            return value;
        }

        const language =
            getLanguage();

        return (
            translations[language]
                ?.topics?.[value]
            ??
            translations.de
                ?.topics?.[value]
            ??
            value
        );
    }


    // =========================================================
    // DATE
    // =========================================================

    function locale() {

        return (
            LOCALES[getLanguage()]
            ||
            LOCALES.de
        );
    }


    function formatDate(value) {

        if (!value) {
            return "";
        }

        /*
         * LocalDate values such as 2026-10-01 should not be
         * shifted by a browser timezone. Therefore the date
         * components are parsed explicitly.
         */
        const localDateMatch =
            /^(\d{4})-(\d{2})-(\d{2})$/
                .exec(value);

        let date;

        if (localDateMatch) {

            date =
                new Date(
                    Number(localDateMatch[1]),
                    Number(localDateMatch[2]) - 1,
                    Number(localDateMatch[3])
                );

        } else {

            date =
                new Date(value);
        }

        if (
            Number.isNaN(
                date.getTime()
            )
        ) {

            return value;
        }

        return new Intl.DateTimeFormat(
            locale(),
            {
                day: "2-digit",
                month: "2-digit",
                year: "numeric"
            }
        ).format(date);
    }


    // =========================================================
    // STATIC DOM TRANSLATION
    // =========================================================

    function applyStaticTranslations(
        root = document
    ) {

        root
            .querySelectorAll(
                "[data-i18n]"
            )
            .forEach(element => {

                const key =
                    element.dataset.i18n;

                element.textContent =
                    t(key);
            });


        root
            .querySelectorAll(
                "[data-i18n-placeholder]"
            )
            .forEach(element => {

                const key =
                    element.dataset
                        .i18nPlaceholder;

                element.placeholder =
                    t(key);
            });


        root
            .querySelectorAll(
                "[data-i18n-aria-label]"
            )
            .forEach(element => {

                const key =
                    element.dataset
                        .i18nAriaLabel;

                element.setAttribute(
                    "aria-label",
                    t(key)
                );
            });


        root
            .querySelectorAll(
                "[data-topic-label]"
            )
            .forEach(element => {

                const topicName =
                    element.dataset
                        .topicLabel;

                element.textContent =
                    topic(topicName);
            });


        root
            .querySelectorAll(
                "select option[value]"
            )
            .forEach(option => {

                if (!option.value) {
                    return;
                }

                const translated =
                    topic(
                        option.value
                    );

                if (
                    translated !==
                    option.value
                ) {

                    option.textContent =
                        translated;
                }
            });
    }


    // =========================================================
    // LANGUAGE BUTTONS
    // =========================================================

    function updateLanguageButtons(
        root = document
    ) {

        const currentLanguage =
            getLanguage();

        root
            .querySelectorAll(
                ".language-button"
            )
            .forEach(button => {

                const active =
                    button.dataset.language ===
                    currentLanguage;

                button.classList.toggle(
                    "active",
                    active
                );

                button.setAttribute(
                    "aria-pressed",
                    String(active)
                );
            });
    }


    // =========================================================
    // INITIALIZATION
    // =========================================================

    function initialize() {

        document.documentElement.lang =
            getLanguage();
    }


    initialize();


    return {

        getLanguage,
        setLanguage,

        t,
        topic,

        locale,
        formatDate,

        applyStaticTranslations,
        updateLanguageButtons
    };

})();