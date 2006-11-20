/**
 * JFritz
 * http://jfritz.sourceforge.net/
 * 
 *
 * (c) Arno Willig <akw@thinkwiki.org>
 * 
 * Created on 08.04.2005
 *
 * Authors working on the project:
 * 		akw			Arno Willig <akw@thinkwiki.org>
 * 		robotniko	Robert Palmer <robotniko@gmx.de>
 * 		kleinc		Christian Klein <kleinch@users.sourceforge.net>
 *      little_ben  Benjamin Schmitt <little_ben@users.sourceforge.net>
 *      baefer		Bastian Schaefer <baefer@users.sourceforge.net>
 *      capncrunch	Brian Jensen <capncrunch@users.sourceforge.net>
 * 
 * This tool is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This piece of software is distributed in the hope that it will be 
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * 
 * BUGS: bitte bei Sourceforge nachschauen und dort auch den Status �ndern
 * BUGS: http://sourceforge.net/tracker/?group_id=138196&atid=741413
 * 
 * FeatureRequests: bitte bei Sourceforge nachschauen und dort auch den Status �ndern
 * FeatureRequests: http://sourceforge.net/tracker/?func=browse&group_id=138196&atid=741416
 
 * 
 * (TODO: Checken, ob alle Bibliotheken vorhanden sind)
 * (TODO: Neue Kurzwahlen von der Beta-FW ins Telefonbuch aufnehmen)
 * (TODO: Import der Anrufliste im XML-Format beim Kontextmen� einbauen)
 * TODO: Language-Files checken, ob tats�chlich alle Werte ben�tigt werden
 * TODO: Sonderzeichen werden in den Balloontips unter Windows nicht korrekt angezeigt. Scheint ein Windowsproblem zu sein. L�sung/Workaround noch nicht gefunden.
 * TODO: JFritz.ico mitliefern
 * TODO: Linux-Startscript mitliefern
 * TODO: LANG/jfritz.properties bei einem neuen Release aktuell halten
 * TODO: Vor dem Release noch den installDirectory-Pfad in JFritzUpdate auf "." anpassen
 * 
 * Roadmap:
 * JFritz 1.0
 * Bewertung - Feature
 * rob - brian
 * 10 - 10 - Fehlermeldung an den Benutzer, wenn Daten nicht auf Festplatte gespeichert werden k�nnen. (Vielleicht schon implementiert -- Rob)
 * 10 - 9 - "Verbindungsger�t" in "MSN/Rufnummer" �ndern
 * 10 - 10 - Kommentarspalte im Telefonbuch
 * 10 - 7 - Webverkehr �ber Proxy (Was f�r Proxys sind gemeint: Socks 4 /5, oder HTTP(S)?)
 * 10 - 10 - Einstellen der Landes- und Ortsvorwahlen pro SIP-Account und nicht nur global (SF [ 1438932 ])
 * 9 -  8 - Export des gesamten Adressbuchs als VCard (http://www.ip-phone-forum.de/showthread.php?t=106758)
 * 9 -  9 - Einstellungen-Seiten �berarbeiten.       Gr��e ver�nderbar machen!(bei modalen Dialoge geht das nicht)  
 * 9 -  9 - Name f�r die Nebenstellen aus der Weboberfl�che auslesen und zuweisen (SF [ 1498487 ])
 * 9 -  7 - Vollst�ndiger Outlook-Support (SF [ 1498489 ])
 * 8 -  8 - Analoge Rufnummer aus der FritzBox auslesen
 * 7 -  7 - Einige Icons auslagern - unterschiedliche Icon-Packs
 * 7 -  7 - Sounddateien auslagern - unterschiedliche Sound-Packs (gute Ideen, Brian)
 * 7 -  7 - Popup und Tray-Message f�r Anrufmonitor anpassbar machen (Name, Nummer, Adresse, Nebenstelle, Stadt, "von Arbeit", "von SIP", anderer Text, Gr��e des Popups)
 * 7 -  5 - CSV-Export nicht nur mit ";", sondern auch mit "TAB", "SPACE" und "," (SF [ 1509248 ])
 * 6 -  8 - Synchronisierung von JFritz Telefonbuch und FritzBox Telefonbuch (SF [ 1494436 ])
 * 6 -  8 - Datumsfilter konfigurierbar gestalten (SF [ 1498488 ])
 * 6 -  6 - Internationalisierung abschlie�en, drunter Flaggencode optimieren (Nummer <-> flaggenfile Zuordnung in einer Hashmap ablegen).
 * 5 -  5 - Anrufmonitor: Anrufmonitor m�chtiger machen (Aktionen nur f�r best. Nummern, verschiedene Aktionen, Log der Anrufe, Notizen zu einem laufenden Anruf) (SF [ 1525107 ])
 * 5 -  5 - Signalisieren der neu eingegangenen Anrufe im Tray (blinken, oder Zahl)
 * 5 -  5 - Button zum L�schen der Anrufliste
 * 5 -  7 - Mehrere FritzBoxen abfragen (SF [ 1515855 ]) Daf�r sollten wir alle zugriffe auf die Box in eigene Threads unterbringen. 
 *                      Dann w�rde JFritz sich beim Hochfahren nicht so lange verz�gern, wenn die Box nicht erreichbar ist.  
 * 4 -  5 - Visualisierung der aktuellen Gespr�che (Frei, Nummer, Name, Dauer des Gespr�chs ...)
 * 4 -  4 - Plugins (M�gliche Plugins: Drucken, Anrufmonitor)
 * 4 -  2 - Import vom Tool Fritzinfo (http://www.ip-phone-forum.de/showthread.php?t=101090)
 * 4 -  4 - Begrenzen der Anzeige der Anrufe in der Anrufliste (z.B. maximal 100 Eintr�ge)
 * 4 -  4 - CSV-Export anpassbar machen (wie bei Thunderbird).
 * 4 -  4 - Registrierstatus der VoIP-Provider (SF [ 1315159 ])
 * 4 -  1 - Einstellen der Farben, Symbolleisten, Schriftart, -gr��e (SF [ 1458892 ])
 * 4 -  3 - Exportieren/Anzeige der Anrufliste nach Monaten getrennt
 * 3 -  2 - SQL-Anbindung (SF [ 1515305 ])
 * 3 -  3 - Anzeige des letzten Telefonats nicht nur abh�ngig von der Standardnummer und anzeige der gesprochenen Minuten pro Telefonbucheintrag
 * 3 -  2 - Tastaturk�rzel f�r Aktionen sollen editierbar sein
 * 3 -  2 - Netzwerkfunktionen (Client/Server) (SF [ 1485417 ]) Das wird das allerschwierigste von allen, und am meisten Planung ben�tigen.
 * 3 -  2 - Spalte "Privatkontakt" in CSV-Liste hinzuf�gen (SF [ 1480617 ])
 * 2 -  2 - 64-bit Unterst�tzung
 * 1 -  1 - LDAP-Anbindung
 * 1 -  1 - SMS Benachrichtigung (�ber Festnetzgateway)
 * 1 -  1 - Style-Sheet f�r die Anzeige der Anrufliste als HTML
 * 1 -  1 - Einige ausgew�hlte Statisken �ber die DSL benutzung, damit JFritz eine komplette L�sung f�r die Fritz!Box anbietet.
 * 1 -  1 - umstieg auf Mustang, damit verbunden jdic rauswerfen und nur noch Java-interne Bibliotheken nutzen
 * 					Gut, dann k�nnen wir endlich diese ganze String.indexOf('@') > 0 rausschmei�en :)
 *                  Das w�rde aber heissen, dass wir nicht mehr zu Java 1.4 kompatibel sind. Einige Plattformen (wie MAC) werden 
 *                      bestimmt noch �ber l�ngere Zeit kein Java SE 6 anbieten. -- Rob
 * 1 -  1 - Statistikfunktionen
 * 1 -  1 - WAN IP beim Tray-Icon anzeigen lassen ?
 * 1 -  1 - Skinns (SF [ 1471202 ])
 * 1 - (-1) - Unterst�tzung f�r das Adressbuch von Lotus Notes (SF [ 1445456 ]) (Ich bin dagegen, denn man br�uchte nochmal so ne Plugin wie bei Outlook, 
 * 						nur ich sch�tze es gibt gar keins => wir m�ssten eine schreiben.
 * 						Habe das programm bei mir in der Arbeit, und ich hasse es. Ich werde nicht mehr Zeit als notwendig ist damit verbringen.
 * 1 -  1 - Bild / Rufton / Farbe eines bestimmten Anrufers
 * 
 * 
 * CHANGELOG:
 *
 * Jfritz 0.6.2
 * TODO:
 * - Alle Zugriffe auf FritzBox in eigenen Threads
 * - Bug: Eingabe der IP-Nummer nach Ruhezustand
 * - Markieren der Zeilen per STRG auch in der "Teilnehmer"-Spalte
 * - �ndern der Standardrufnummer per H�ckchen f�hrt nicht zur Speicherung, erst wenn man noch ein Datum �ndert
 * - Filter f�r Nebenstelle (Port) kombiniert mit eingetragenem Namen in der Weboberfl�che
 * - Copy & Paste f�r Spalteneintr�ge (in Anrufliste, Telefonbuch und Kurzwahlliste)
 * - Importierte Rufnummern auf Sonderzeichen ( -, /, (, ) ) �berpr�fen
 * - Durchwahlnummern vor Zentrale-Nummern bei der Anzeige bevorzugen (sollte eigentlich gehen, aber scheint einen Bug zu haben)
 * - Schnittstelle zu externen Inverssuche-Programmen
 * - Andere Anrufmonitore noch an die neuen Listener anpassen und TestCases schreiben
 * - Kurzwahlliste sortierbar und Spaltenreihenfolge �nderbar
 * - Bug "Doppelt erfasste Anrufe" behoben?
 * - http://www.ip-phone-forum.de/showthread.php?t=112348
 * - 0900 Nummern werden nicht korrekt erkannt http://www.ip-phone-forum.de/showthread.php?t=114325 => Liste mit Call-By-Call Vorwahlen
 * - �berpr�fen, geht wohl nicht mehr: R�ckw�rtssuche f�r �sterreich �ber dasoertliche.de wieder eingebaut
 * - Connection-Timeout f�r ReverseLookup setzen
 * TODO-ENDE
 *  
 * - Neue Strings:
 *  check_for_new_version_after_start
 *  date_filter_last_week
 *  date_filter_this_week
 *  enable_inet_monitoring
 *  filter_search
 *  inet_usage
 *  monitoring
 *  restart_telefond (only english version)
 *  restart_telefond_desc (only english version)
 *  undo
 *  update_JFritz
 *  Alle Strings in der Datei update_de_DE.properties
 *  
 * - Bugfix: �rtliche Nummer, die mit 49 beginnen, werden jetzt richtig verarbeitet
 * - Bugfix: Callmonitor schreibt die Ortsvorwahl vor unbekannten Rufnummern nicht mehr
 * - Neu: MonitoringPanel hinzugef�gt, soll Internetverbindung und derzeit gef�hrten Anrufen �berwachen
 * - Suchfeld in Anrufliste umfasst nun auch die Call-By-Call vorwahlen
 * - Bugfix: Suche nach Rufnummern im internationalen Format
 * - Neu: JFritz-Fenster wird nun korrekt wiederhergestellt (maximiert...). Neues Property: window.state
 * - Neu: Falls Ort per ReverseLookup nicht gefunden wird, wird anhand einer Tabelle der passende Ort zu einer Vorwahl eingetragen werden (�sterreich) 
 * - Neu: Falls Ort per ReverseLookup nicht gefunden wird, wird anhand einer Tabelle der passende Ort zu einer Vorwahl eingetragen werden Deutschland (SF [ 1315144 ]) 
 * - Bugfix: Jetzt werden IP-Addressen von den Boxen in der Einstellungen angezeigt. Man kann jetzt Fehlerfrei zwei boxes im gleichen Netz haben. 
 * - Neu: R�ckw�rtssuche f�r die USA �ber www.whitepages.com, danke an Reiner Gebhardt
 * - Neu: Men�eintrag ->JFritz aktualisieren
 * - Internationaler FreeCall 00800 (http://www.ip-phone-forum.de/showthread.php?t=111645)
 * - Datumsfilter "aktueller-Tag" sollte auch immer den aktuellen Tag anzeigen. (SF [ 1530172 ])
 * - INTERN: Filter der Anrufliste in neues Package. Abstrakte Klasse CallFilter
 * - INTERN: Statische Methoden in JFritz.java => keine jfritz-Referenzen in den anderen Klassen notwendig
 * - INTERN: Diverse JUnit-TestCases
 * - INTERN: Neue Klasse CallMonitoring, die alle aktuellen Anrufe verwaltet und die Anrufinformation auf den Bildschirm bringt
 * - INTERN: Anrufmonitore in neues Package callmonitor gepackt und umbenannt
 * - INTERN: Anzeige der Anrufe, die vom Anrufmonitor erkannt werden, �ber Listener. Abholen der Anrufliste nach dem Gespr�chsende nun �ber den DisconnectMonitor
 * - Neu: Unterst�tzung f�r die "Labor-Firmware" (Firmwareerkennung und CSV-Import)
 * - Neu: Automatisches Update von JFritz auf neue Version. TODO: Checken, ob es auch mit .so und .dll funktioniert TODO: Am Ende der Update-Prozedur JFritz nicht beenden, sondern neu starten. Blos wie? (mittles eines kleinen zus�tlichen programmes, welches die files ersetzt und jfritz neu startet. datenaustausch der programme evtl per kommandozeile. (bastia))
 * - Wahlhilfe: Immer mit Ortsvorwahl w�hlen (getShortNumber in getAreaNumber ver�ndert)
 * - Neu: Unterst�tzung f�r neue englische Firmware xx.04.20
 * - INTERN: Buildfile �berarbeitet. TODO: release und junit anpassen
 * - Neu: R�ckw�rtssuche nicht mehr �ber DasOertliche.de sondern �ber dastelefonbuch.de
 * - Bugifx: Franz�sische R�ckw�rtssuche funktioniert wieder
 * - Neu: Franz�sische R�ckw�rtssuche nun auch mit Firmenname
 * - Intern: JFritz.java aufgespalten in Main.java und JFritz.java
 * - Autoupdate von JFritz
 * - Nur noch mit Java 1.5 kompatibel
 * - Fenstergr��e, -position und -status wird nun korrekt wiederhergestellt
 * - Spaltengr��e und -reihenfolge korrekt wiederhergestellt
 * - Bugfix: Telefonnummern werden nun korrekt gespeichert
 * - Bugfix: Importieren von Thunderbird-Eintr�gen funktioniert nun wieder
 *
 * JFritz 0.6.1
 * - Neue Strings: 
 *		browse
 *		save_directory
 *		delete_duplicate_phonebook_entries
 *		delete_duplicate_phonebook_entries_confirm_msg
 *		delete_duplicate_phonebook_entries_inform_msg
 *		box.port
 *		config_wizard_info1
 *		config_wizard_info2
 *		config_wizard_info3
 *		config_wizard_info4
 *		config_wizard
 *		popup_delay
 *      dial_prefix
 *      
 * - Intern: Multiple-Instance-Lock nun Dateibasiert und nicht mehr als Property. (JFritz.LOCK_FILE)
 * - Neu: Meldung bei neuer JFritz-Version     
 * - Neu: Flaggen werden bei bekannten L�ndervorw�hlen angezeigt anstelle vom Weltkugel, f�r bekannte L�nder siehe PhoneNumber.java
 * - Bugfix: SIP-Routen behalten ihre historische Zuordnung      
 * - Neu: Neuer Kommandozeilenparameter: -r, f�hrt eine R�ckw�rtssuche aus und beendet sich
 * - Neu: R�ckw�rtssuche f�r Frankreich �ber http://www.annuaireinverse.com, wird automatisch aufgerufen
 * - Neu: R�ckw�rtssuche f�r die Niederlande �ber http://www.gebeld.nl/content.asp?zoek=numm, wird automatisch aufgerufen
 * - Neu: R�ckw�rtssuche f�r Italien �ber www.paginebianche.it, wird automatisch aufgerufen     
 * - Neu: R�ckw�rtssuche f�r die Schweiz �ber tel.search.ch, JFritz ruft automatisch die richtige R�ckw�rtssuche auf.
 * - Neu: Dummy-Telefonbucheintr�ge werden gel�scht, falls ein Eintrag mit derselben Nummer existiert 
 * - Neu: Anrufe mit einer AKZ werden jetzt richtig verarbeitet.
 * - Neu: Der Speicherordner kann jetzt frei gew�hlt werden, bleibt nach dem Beenden erhalten. SF-Tracker [1248965]
 * - Bugfix: Die Sprachauswahlbox zeigt jetzt auch unter Linux Flaggen-Icons an.
 * - Bugfix: Das Telefonbuch wird nach einem erfolgreichen Outlook-Import sofort gespeichert. SF-Tracker [ 1503185 ]
 * - Neu: Zeit, bis Popup-Nachrichten ausgeblendet sind, einstellbar gemacht (Zeit von 0 bedeutet nie schlie�en) SF-Request Nr: [1340678] [1518330]
 * - Bugfix: JFritz kann jetzt von einem beliebigen Verzeichnis aus aufgerufen (best�tigt unter Linux, Windows, Mac??)
 * - Neu: R�ckw�rtssuche auch f�r Handynummern
 * - Neu: W�hlhilfe merkt sich den zuletzt benutzen Port
 * - Neu: JFritz kann jetzt beliebige Nummer mit der Wahlhilfe w�hlen (noch nicht ausf�hrlich getestet, z.B. funktionieren auch die Tastencode?)
 * - Bugfix: Kurzwahlen werden jetzt korrekt geparst beim Abholen der Anrufliste
 * - Neu: Port einstellbar
 * - Neu: Konfigurationswizard f�r Erstbenutzer
 * - Neu: Logfiles werden jetzt mittels Stream redirection geschrieben (hei�t auch die Exceptions werden in den Logfiles aufgenommen :) )
 * - Neu: Entfernen doppelter Eintr�ge beim Telefonbuch
 * - Neu: Automatisches Scrollen zum selektierten Telefonbucheintrag
 * - Neu: Englische Firmware wird unterst�tzt
 * - Intern: Firmware wird beim Start erkannt und in JFritz.firmware gespeichert. Zugriff nicht mehr �ber JFritz.getProperties("box.firmware") sondern �ber JFritz.getFirmware()
 * - Bugfix: Kurzwahlen werden wieder korrekt abgeholt
 * - Bugfix: Standardtelefonnummern k�nnen wieder ge�ndert werden
 * - Bugfix: Problem mit dem Holen der Anrufliste behoben
 * - Bugfix: Nebenstellenbezeichnungen und Route k�nnen jetzt Sonderzeichen enthalten
 * - Bugfix: Anzeige eines analogen Anrufs beim Anrufmonitor 
 * - Bugfix: PersonDialog ("Person editieren") wird nun mittig zum JFritz.JFrame angezeigt - SF.net-Request:[1503523] Adress-/Telefonbuch 
 * - Neu: Default- und Close-Button f�r PersonDialog ("Person editieren"), Icon (JFritz) gesetzt
 * - Bugfix: Wahlhilfe: Anwahl aller analogen Telefone konnte nicht gehen -> Tippfehler in JFritzUtils: JFritz.getMessage("analoge_telephones_all") -> korrigiert in JFritz.getMessage("analog_telephones_all")
 * - Neu: Default-Button bei R�ckfrage 'Box-Anruferliste l�schen' ge�ndert auf 'Nein'  
 * - Neu: Ber�cksichtigung der Metal-Decorations bei Dialogen
 * - Intern: Funktionen, die mit der Kommunikation mit der FritzBox zu tun hatten, in eine neue Klasse FritzBox exportiert.
 * - Intern: CallDialog: Auswahl der Nummern wiederhergestellt, editierbare JComboBox/JTextField (je nach Anzahl vorhandener Nummern)
 * - Neu: Default- und Close-Button f�r CallDialog ("Anrufen"), Icon (JFritz) gesetzt
 * - Neu: �berarbeitung der Dialoge bzgl. OK/Cancel, Icon, Position
 *   jfritz.dialogs.config.CallmessageDialog, 
 *   jfritz.dialogs.config.ConfigDialog, 
 *   jfritz.dialogs.config.FRITZBOXConfigDialog, 
 *   jfritz.dialogs.config.SipConfigDialog, 
 *   jfritz.dialogs.config.SyslogConfigDialog, 
 *   jfritz.dialogs.config.TelnetConfigDialog, 
 *   dialogs.config.YacConfigDialog,
 *   
 *   jfritz.dialogs.simple.AddressPasswordDialog,
 *   jfritz.dialogs.stats.StatsDialog,
 *   
 *   jfritz.callerlist.CallDialog, 
 *   
 *   jfritz.utils.ImportOutlookContacts, 
 *   jfritz.utils.NoticeDialog
 *   
 *  
 * JFritz 0.6.0
 * - Neue Strings:
 * - Neuer Kommandozeilenparameter: -w, deaktiviert die Kontrolle von mehrfachen Instanzen
 * - Bugfix: Alle internationalen Gespr�che werden jetzt erkannt.
 * - Neu: Sprache einstellbar ( <- Wahlhilfe im Telefonbuch funktioniert bei englischer Sprache nicht (Bastian)
 * 								<- TrayMenu angepasst (Benjamin)
 * 								<- komplett ge�ndert, Sprachfiles werden jetzt dynamisch erkannt und k�nnen in den Einstellungen ausgew�hlt werden. (Bastian))
 * - Bugfix: Spracheinstellungen werden gespeichert.
 * - Neu: Verbesserte Anzeige des aus- und eingehenden Verbindungstyps bei verwendung des JFritz-Anrufmonitors im Format "interne MSN (Leitungsart)", z.B. "1234 (ISDN)" oder "1234 (SIP)" bei eingehenden Anrufen oder "56789 (88sdg4@dus.net)" bei ausgehenden
 * - Bugfix: Anrufmonitor zeigt ausgehende und eingehende Anrufe im gleichen Format an 
 * - Bugfix: Neues JFritz-Anrufmonitor-Format besser unterst�tzt, jetzt wieder Anzeige von angerufener MSN
 * - Bugfix: MAC-Handling funktioniert wieder
 * - Bugfix: Wahlhilfe im Telefonbuch funktioniert jetzt bei englischer Sprache (Brian) 
 * - Bugfix: Beim Metal-LAF werden jetzt immer die Metal-Decorations verwendet.
 * - Bugfix: Beim �ndern des Look And Feel's werden die Buttons korrekt dargestellt.
 * - Neu: Fritzbox Anrufliste als CSV-Datei importieren
 * - Neu: Thunderbird/Mozilla-Kontakte importieren
 * - Neu: Telefonbuch als CSV-Datei exportieren
 * - Neu: Anruferliste importieren (CSV-Dateien)
 * - Neu: Wahlhilfe (<- funktioniert nicht richtig. Es wird immer der Port vom letzten Versuch benutzt
 *                      Beispiel: ich habe zuletzt ISDN 1 benutzt, will jetzt mit ISDN 2 anrufen, dann wird aber ISDN 1 benutzt.
 *                      Benutze ich dann die Wahlhilfe erneut, wird ISDN 2 benutzt - egal welchen Port ich einstelle. D.h., benutze
 *                      ich st�ndig die gleichen Ports, f�llt es nicht weiter auf.
 *                      Ich denke, das h�ngt damit zusammen, dass man auf der Weboberfl�che erst den Port ausw�hlt, dann �bernehmen
 *                      dr�ckt und dann erst die Nummer anklickt. Diesen Vorgang m�sste man in JFritz nachbilden.  (KCh)
 *                   <- Eigentlich sollte es auch mit einem direkten URL-Aufruf funktionieren. Machen andere Tools genau so. (Robert)
 *                   <- Kann denn keiner das Verhalten meiner Box nachvollziehen? Ist das evtl. ISDN-spezifisch? Ich kanns es 100%ig reproduzieren (KCh))
 * - Neu: (JFritz)Telefonbuch importieren (XML)
 * - Neu: Manuelle Backups erstellen (Men� und Toolbar)
 * - Neu: per Funktionstaste "F5" Anrufliste aktualisieren
 * - Neu: Suchfunktion f�r Telefonbuch
 * - Neue Option: Nach Standby oder Ruhezustand die Anrufliste automatisch abholen
 * - Neue Option: Sicherungskopien bei jedem Laden der Anruferliste erstellen
 * - �nderung: Das Durchsuchen der Anruferliste muss nun per [ENTER] gestartet werden.
 * - Bugfix: "�bernehmen" Button im Telefonbuch wird nun anklickbar, wenn man eine Telefonnummer ge�ndert hat.
 * - Bugfix: Sonderzeichen bei "Externes Programm starten" werden korrekt gespeichert 
 * - Bugfix: tritt der unwahrscheinliche Fall auf, dass kein Tray-Icon angezeigt wird, der User aber fr�her einmal
 * 			 (als das Tray-Icon noch verf�gbar war) Tray-Messages zu Benachrichtigung ausgew�hlt hatte, wurde gar kein
 * 			 Anruf mehr signalisiert. Jetzt wird in diesem Fall auf ein PopUp zur�ckgegriffen.
 * - Bugfix: unvollst�ndige Anzeige des Einstellungsdialoges -> Weiteres
 * - Bugfix: Speicherung der Kommentare
 * - Bugfix: �berschreiben der Rufnummer im Telefonbuch tritt nicht mehr auf
 * - INTERN: Bereitstellen von utils.JFritzClipboard und CallerList.getSelectedCall
 * - INTERN: JDIC-Update auf "JDIC 20050930 Build"
 * 
 * JFritz 0.5.5
 * - Nummer und Anschrift k�nnen aus der Anrufliste heraus in die Zwischenablage kopiert werden
 * - Schutz vor mehrfachem Programmstart (<- was ist mit Kommandozeilenstart?, =>BS: werden ber�cksichtigt - enableInstanceControl=false)
 * - L�schfunktionalit�t f�r Anrufliste der FRITZ!Box (Men� und Toolbar)
 * - Bugfix: Start auch bei fehlendem Tray
 * - Bugfix: Anrufmonitor arbeitete bei einem Reverselookup einer nicht im Telefonbuch 
 *           eingetragenen Person nicht mehr
 * - Bugfix: Eintragen einer �ber Reverse-Lookup gefundenen Person korrigiert
 * - Neuer Kommandozeilenparameter: -d, --delete_on_box, l�scht Anrufliste auf der Box und beendet sich dann (kein GUI)
 * - Neuer Kommandozeilenparameter: -b, --backup, erstellt eine Sicherungskopie von allen XML-Dateien
 * - Neue Option: Sicherungskopien beim Start erstellen
 * - Bugfix: Bei der Suche nach einer Rufnummer werden vor der Zentrale ggf. vorhandene Durchwahlnummern ber�cksichtigt 
 *  
 * JFritz 0.5.4
 * - Beim neuen Anrufmonitor auf # achten.
 * - Callmonitor: Beim Ausf�hren eines externen Programmes werden %Firstname, %Surname, %Compnay ersetzt.
 * - Beim Beenden von JFritz keine Speicherung von Calls und Phonebook mehr
 * - Bei den Einstellungen die MAC weggenommen
 * - Bugfix: Sonderzeichen bei "Externes Programm starten" werden korrekt gespeichert 
 * - Watchdog: Anrufmonitor wird nach dem Ruhezustand neu gestartet
 * - Anrufliste wird per CSV und nicht mehr per Webinterface abgeholt
 * - Unterst�tzung f�r Firmware xx.04.03
 * 
 * JFritz 0.5.3
 * - Bugfix-Anrufmonitor: Nummern werden internationalisiert
 * 
 * JFritz 0.5.2
 * - Parameter -n funktioniert wieder
 * - XML-Dateien angepasst. DTDs werden nicht mehr gespeichert. Kann zu Datenverlust kommen
 * - Kompatibel zur Firmware xx.04.01
 * - FRITZ!Box-Anrufmonitor: Abholen der Anrufliste nach dem Auflegen
 * 
 * JFritz 0.5.1
 * - Priorit�t auf 5 erh�ht
 * - Kompatibel zur Firmware xx.03.101
 * - Datenverbindungen werden als solche angezeigt
 * - Outlookimport verbessert
 * 
 * JFritz 0.5.0
 * - Neuer Anrufmonitor: FRITZ!Box Anrufmonitor
 * - Kompatibel zur Firmware xx.03.99
 * - Einstelloption f�r "minimieren statt schlie�en"
 * 
 * JFritz 0.4.7
 * - New Feature: Variable Programmpriorit�t (1..10)
 * - Neuer Kommandozeilenparameter -p5 --priority=5
 * - Kompatibel zur FRITZ!Box 7170
 * - Anzeige der Gesamtgespr�chsdauer in Stunden und Minuten
 * - Bugfix: Manche Spalten lie�en sich nicht klein genug machen
 * - Bugfix: Kommandozeilenparameter -c funktionierte nicht mehr
 * - Bugfix: Outlook-Import
 * - Bugfix: RESSOURCES: filter_callbycall, filter_sip
 * - Bugfix: Telefonbuchsortierung
 * 
 * JFritz 0.4.6
 * - Reset-Button bei den Filtern deaktiviert alle Filter
 * - Neuer Filter: Kontextmen� bei "Verpasste Anrufe"-Filter
 * - Neuer Filter: Kommentarfilter
 * - Neuer Befehl f�r "Anrufmonitor - Externes Programm starten": %URLENCODE();
 * - Kompatibel zu FritzBox 5010 und 5012
 * - Automatische Erkennung der Firmware
 * - Bugfix: Danisahne-Mod wird richtig erkannt
 * - Bugfix: Outlook-Import (entfernen von Klammern)
 * - Bugfix: Anzeigefehler beim Start behoben
 * - Bugfix: Sortierfunktion beim Telefonbuch korrigiert
 * 
 * JFritz 0.4.5
 * - Unterst�tzung f�r FRITZ!Box Firmware .85
 * - Unterst�tzung f�r FRITZ!Box Firmware .87
 * - Unterst�tzung f�r FRITZ!Box Firmware .88
 * - Spalten sind jetzt frei verschiebbar
 * - Kommentarspalte hinzugef�gt
 * - Kommentar- und Anschlu�-Spalte k�nnen ausgeblendet werden
 * - Suche der FritzBox �ber UPNP/SSDP abschaltbar
 * - Telefonbuch nun nach allen Spalten sortierbar
 * - Beim Export merkt sich JFritz die Verzeichnisse
 * - Drucken der Anrufliste (und Export nach Excel, RTF, PDF, CSV, ...)
 * - Neue Kommandozeilenoption -n: Schaltet die Tray-Unterst�tzung aus
 * - Direkter Import von Outlook-Kontakten
 * - Datumsfilter unterst�tzt nun "Gestern"
 * - Unterst�tzung f�r die neue Version des Callmessage-Anrufomitors (http://www.evil-dead.org/traymessage/index.php4)
 * - Bugfix: Firmware konnte beim ersten Start nicht erkannt werden
 * - Bugfix: Spaltenbreite wurde nicht korrekt gespeichert
 * - Bugfix: Falsche SIP-ID bei gel�schten Eintr�gen
 * - Bugfix: Wenn Kurzwahl unbekannt war, wurde eine falsche Rufnummer angezeigt
 * - Bugfix: Anrufliste wird nur gel�scht, wenn mind. 1 Eintrag abgeholt wurde
 * 
 * Internal: 
 * - SipProvider-Informationen werden nicht mehr in den 
 * 	 jfritz.properties.xml sondern in jfritz.sipprovider.xml
 *   gespeichert.
 * - Zugriff auf SipProvider �ber JFritz.getSIPProviderTableModel() 
 * 
 * JFritz 0.4.4
 * - CallByCall information is saved (only 010xy and 0100yy)
 * - Added support for MacOSX Application Menu
 * - Telnet: Timeout handling
 * - Telnet-Callmonitor: support for username, password
 * - Syslog-Callmonitor: syslogd and telefond check configurable
 * - Added Callmessage-Callmonitor. See Thread-Nr. 178199 in IPPF
 * - Wait, when no network reachable (On startup, return of standby, ...)
 * - Added context menu to phonebook and callerlist
 * - New Callfilter: Route, Fixed call, CallByCall
 * - New Datefilter: Right click on date filter button
 * - Display more information in status barm Zielfon h�r ich "Ihre Nummerwird gehalten...". Bitte Einbauen!! Das ist der Hammer!

 * - Export to XML
 * - Export CallByCall to CSV
 * - Phonenumber with wildcard support (PhoneNumber-Type "main")
 * - Start external Program on incoming call (%Number, %Name, %Called)
 * - Bugfix: Syslog-Monitor get Callerlist on Restart
 * - Bugfix: Check for double entries in Callerlist
 * - Bugfix: Reverselookup on call
 * 
 * Internal:
 * - VCard Export moved from CallerTable to PhoneBook
 * 
 * 
 * JFritz 0.4.2
 * - CallByCall information is saved
 * - Added Phonebookfilter (Private Phonebook)
 * - Callerlist deleteable
 * - Advanced CSV-File
 * - Callmonitor with Telnet, Syslog, YAC
 * - Syslog passthrough
 * - CMD Option -e : Export CSV
 * - CMD Option -c : Clear Callerlist
 * - CMD Option -l : Debug to Logfile
 * - Bugfix: Statistic-Dialog uses box.ip not 192.168.178.1
 * - Bugfix: Compatibility to Java 1.4.2
 * - Bugfix: Passwords with special chars
 * - Bugfix: Some charset bugfixing
 * - Bugfix: Phonebook XML-Saving fixed (UTF-8 coding)
 * 
 * 
 * JFritz 0.4.0
 * - Systray minimizes JFrame
 * - Mobile filter inverted
 * - Removed participant support in favour of person
 * - Phonebook support
 * - Added commandline option --fetch
 * - Rewrote xml handler for phonebook
 * - Data statistics
 * - Call monitor with sound notification
 * - Crypted password
 * - Option for password check on program start
 * - Option for disabling sounds
 * 
 * Internal:
 * - Added PhoneNumber class
 * - Added PhoneType class
 * - Restructured packages
 * 
 * 
 * JFritz 0.3.6
 * - New mobile phone filter feature
 * - Systray support for Linux/Solaris/Windows
 * - Systray ballon messages for Linux/Solaris/Windows
 * - Browser opening on Unix platforms
 * - Bugfix: Call with same timestamp are collected
 * 
 * JFritz 0.3.4
 * - New search filter feature
 * - New date and date range filter feature
 * - Sorting of columns by clicking on column headers
 * - VOIP numbers starting with 49 are now rewritten correctly 
 * - SSDP Autodetection of Fritz!Boxes
 * - QuickDial Management
 * - Selection of multiple rows copies VCards to clipboard
 * - Bugfix: Config-Dialog now saves all values correctly
 * - Bugfix: No empty SIP provider after detection
 * - Bugfix: Save-Dialog on export functions
 * - Code rearrangement
 * 
 * JFritz 0.3.2:
 * - Saves and restores window position/size
 * - Saves and restores width of table columns
 * - CallTypeFilter works now (thanks to robotniko)
 * - Filter option is saved
 * - Added filter for calls without displayed number
 * - Total duration of calls now displayed in status bar
 * 
 * JFritz 0.3.0: Major release
 * - Compatibility for JRE 1.4.2
 * - Severel bugfixes
 * 
 * JFritz 0.2.8: 
 * - Bugfix: Firmware detection had nasty bug
 * - Bugfix: Firmware detection detects modded firmware properly
 * - Bugfix: RegExps adapted for modded firmware
 * - Support for SIP-Provider for fritzbox fon wlan
 * - Notify users whenn calls have been retrieved
 * - CSV Export
 * 
 * JFritz 0.2.6:
 * - Several bugfixes
 * - Support for Fritz!Boxes with modified firmware
 * - Improved config dialog
 * - Improved firmware detection
 * - Initial support f�r SIP-Provider
 * - Firmware/SIP-Provider are saved in config file
 * 
 * JFritz 0.2.4:
 * - Several bugfixes
 * - Improventsment on number resolution
 * - Optimized Reverse Lookup
 * 
 * JFritz 0.2.2:
 * - FRITZ!Box FON WLAN works again and is detected automatically.
 * - Target MSN is displayed
 * - Bugfixes for Reverse Lookup (Mobile phone numbers are filtered now)
 * - Nice icons for calltypes (Regular call, Area call, Mobile call)
 * - Several small bugfixes
 * 
 * JFritz 0.2.0: Major release
 * - Improved GUI, arranged colours for win32 platform
 * - New ToolBar with nice icons
 * - Bugfix: Not all calls had been retrieved from box
 * - Improved reverse lookup
 * - Automatic box detection (does not yet work perfectly)
 * - Internal class restructuring
 *  
 * JFritz 0.1.6:
 * - Calls are now saved in XML format
 * - Support for Fritz!Box 7050
 * 
 * JFritz 0.1.0:
 * - Initial version
 */

package de.moonflower.jfritz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JOptionPane;

import de.moonflower.jfritz.autoupdate.JFritzUpdate;
import de.moonflower.jfritz.autoupdate.Update;
import de.moonflower.jfritz.exceptions.WrongPasswordException;
import de.moonflower.jfritz.struct.FritzBox;
import de.moonflower.jfritz.utils.CLIOption;
import de.moonflower.jfritz.utils.CLIOptions;
import de.moonflower.jfritz.utils.CopyFile;
import de.moonflower.jfritz.utils.Debug;
import de.moonflower.jfritz.utils.Encryption;
import de.moonflower.jfritz.utils.JFritzProperties;
import de.moonflower.jfritz.utils.JFritzUtils;

public class Main {

	public final static String PROGRAM_NAME = "JFritz"; //$NON-NLS-1$

	public final static String PROGRAM_VERSION = "0.6.2"; //$NON-NLS-1$

	public final static String PROGRAM_URL = "http://www.jfritz.org/"; //$NON-NLS-1$

	public final static String JFRITZ_PROJECT = "all members of the JFritz-Team"; 
	
	public final static String PROJECT_ADMIN = "Robert Palmer <robotniko@users.sourceforge.net>"; //$NON-NLS-1$

	public final static String USER_DIR = System.getProperty("user.home")
			+ File.separator + ".jfritz";

	public final static String USER_JFRITZ_FILE = "jfritz.txt";

	public static String SAVE_DIR = System.getProperty("user.dir")
			+ File.separator;

	public static String SAVE_DIR_TEXT = "Save_Directory=";

	public final static String LOCK_FILE = ".lock"; //$NON-NLS-1$

	public final static String PROPERTIES_FILE = "jfritz.properties.xml"; //$NON-NLS-1$

	public static boolean SYSTRAY_SUPPORT = false;

	private static JFritzProperties defaultProperties;

	private static JFritzProperties properties;

	private static ResourceBundle localeMeanings;

	private static ResourceBundle messages;

	private static boolean showConfWizard;

	private static boolean enableInstanceControl = true;

	private static boolean checkSystray = true;

	private static String jfritzHomedir;

	private static JFritz jfritz;

	private CLIOptions options;

	public Main(String[] args) {
		System.out.println(PROGRAM_NAME + " v" + PROGRAM_VERSION //$NON-NLS-1$
				+ " (c) 2005-2006 by " + JFRITZ_PROJECT); //$NON-NLS-1$
		Thread.currentThread().setPriority(5);

		jfritzHomedir = JFritzUtils.getFullPath(".update");
		jfritzHomedir = jfritzHomedir.substring(0, jfritzHomedir.length() - 7);
	}

	/**
	 * Main method for starting JFritz
	 * 
	 * LAST MODIFIED: Brian Jensen 04.06.06 added option to disable mulitple
	 * instance control added a new parameter switch: -w
	 * 
	 * @param args
	 *            Program arguments (-h -v ...)
	 * 
	 */
	public static void main(String[] args) {
		Main main = new Main(args);
		main.initiateCLIParameters();
		main.checkDebugParameters(args);

		// Weitere Initialisierung
		loadSaveDir();

		loadProperties();
		loadMessages(new Locale(getProperty("locale", "en_US"))); //$NON-NLS-1$,  //$NON-NLS-2$
		loadLocaleMeanings(new Locale("int", "INT"));

		saveUpdateProperties();

		jfritz = new JFritz(main);

		main.checkCLIParameters(args);
		main.checkInstanceControl();

		jfritz.createJFrame(showConfWizard);
		// TODO sollten wir das programm nicht hier beenden?
		// while(!shutdown){sleep oder sowas
		// Debug.msg("ENDEN---main.java---DNEND");
	}

	/**
	 * Initialisiert die erlaubten Kommandozeilenparameter
	 * 
	 */
	private void initiateCLIParameters() {
		options = new CLIOptions();

		options.addOption('h', "help", null, "This short description"); //$NON-NLS-1$,  //$NON-NLS-2$,  //$NON-NLS-3$
		CLIOption verboseOption = new CLIOption('v', "verbose", null,
				"Turn on debug information");
		options.addOption(verboseOption); //$NON-NLS-1$,  //$NON-NLS-2$,  //$NON-NLS-3$
		options.addOption('s', "systray", null, "Turn on systray support"); //$NON-NLS-1$,  //$NON-NLS-2$,  //$NON-NLS-3$
		options.addOption('n', "nosystray", null, "Turn off systray support"); //$NON-NLS-1$,  //$NON-NLS-2$,  //$NON-NLS-3$
		options.addOption('f', "fetch", null, "Fetch new calls and exit"); //$NON-NLS-1$,  //$NON-NLS-2$,  //$NON-NLS-3$
		options.addOption('d', "delete_on_box", null, //$NON-NLS-1$,  //$NON-NLS-2$
				"Delete callerlist of the Fritz!Box."); //$NON-NLS-1$
		options.addOption('b', "backup", null,
				"Creates a backup of all xml-Files in the directory 'backup'"); //$NON-NLS-1$,  //$NON-NLS-2$,  //$NON-NLS-3$
		options.addOption('c', "clear_list", null, //$NON-NLS-1$,  //$NON-NLS-2$
				"Clears Caller List and exit"); //$NON-NLS-1$
		options.addOption('e', "export", "filename", //$NON-NLS-1$,  //$NON-NLS-2$,  //$NON-NLS-3$
				"Fetch calls and export to CSV file."); //$NON-NLS-1$
		options
				.addOption('z', "exportForeign", null, //$NON-NLS-1$,  //$NON-NLS-2$
						"Write phonebooks compatible to BIT FBF Dialer and some other callmonitors."); //$NON-NLS-1$		
		options.addOption('l', "logfile", "filename", //$NON-NLS-1$,  //$NON-NLS-2$,  //$NON-NLS-3$
				"Writes debug messages to logfile"); //$NON-NLS-1$,
		options.addOption('p', "priority", "level", //$NON-NLS-1$,  //$NON-NLS-2$,  //$NON-NLS-3$
				"Set program priority [1..10]"); //$NON-NLS-1$
        options.addOption('i',"lang", "language","Set the display language, currently supported: german, english"); //$NON-NLS-1$,  //$NON-NLS-2$,  //$NON-NLS-3$, //$NON-NLS-4$
		options
				.addOption(
						'w',
						"without-control", null, //$NON-NLS-1$,  //$NON-NLS-2$ 
						"Turns off multiple instance control. DON'T USE, unless you know what your are doing"); //$NON-NLS-1$
		options
				.addOption('r', "reverse-lookup", null,
						"Do a reverse lookup and exit. Can be used together with -e -f and -z");
	}

	/**
	 * �berpr�ft, ob die -h, -v oder -l Startparameter gesetzt sind
	 * 
	 * @param args
	 *            Kommandozeilenargumente
	 */
	private void checkDebugParameters(String[] args) {
		Vector foundOptions = options.parseOptions(args);

		// Checke den help, verbose/debug und log-to-file parameter
		Enumeration en = foundOptions.elements();
		while (en.hasMoreElements()) {
			CLIOption option = (CLIOption) en.nextElement();

			switch (option.getShortOption()) {
			case 'h': //$NON-NLS-1$
				System.out.println("Usage: java -jar jfritz.jar [Options]"); //$NON-NLS-1$
				options.printOptions();
				exit(0);
				break;
			case 'v': //$NON-NLS-1$
				Debug.on();
				break;
			case 'l': //$NON-NLS-1$
				String logFilename = option.getParameter();
				if (logFilename == null || logFilename.equals("")) { //$NON-NLS-1$
					System.err.println(getMessage("parameter_not_found")); //$NON-NLS-1$
					exit(0);
				} else {
					Debug.logToFile(logFilename);
					break;
				}
			}
		}
	}

	/**
	 * �berpr�ft die weiteren Kommandozeilenparameter
	 * 
	 * @param args
	 *            Kommandozeilenargumente
	 */
	private void checkCLIParameters(String[] args) {
		boolean shutdown = false;
		Debug.msg("Start commandline parsing"); //$NON-NLS-1$
		// Checke alle weiteren Parameter
		Vector foundOptions = options.parseOptions(args);
		Enumeration en = foundOptions.elements();
		while (en.hasMoreElements()) {
			CLIOption option = (CLIOption) en.nextElement();

			switch (option.getShortOption()) {
			case 'b': //$NON-NLS-1$
				doBackup();
				break;
			case 's': //$NON-NLS-1$
				SYSTRAY_SUPPORT = true;
				break;
			case 'n': //$NON-NLS-1$
				checkSystray = false;
				break;
			case 'f':
				shutdown = true;
				Debug.msg("Fetch caller list ..."); //$NON-NLS-1$
				try {
					JFritz.getCallerList().getNewCalls();
				} catch (WrongPasswordException e) {
					Debug.err(e.toString());
				} catch (IOException e) {
					Debug.err(e.toString());
				}
				break;
			case 'r':
				JFritz.getCallerList().reverseLookup(false);
				shutdown = true;
				break;
			case 'e':
				String csvFileName = option.getParameter();
				if (csvFileName == null || csvFileName.equals("")) { //$NON-NLS-1$
					System.err.println(getMessage("parameter_not_found")); //$NON-NLS-1$
					exit(0);
				}
				Debug.msg("Exporting Call list (csv) to " + csvFileName); //$NON-NLS-1$
				JFritz.getCallerList().saveToCSVFile(csvFileName, true);
				shutdown = true;
				break;
			case 'z':
				JFritz.getPhonebook().saveToBITFBFDialerFormat("bitbook.dat"); //$NON-NLS-1$
				JFritz.getPhonebook()
						.saveToCallMonitorFormat("CallMonitor.adr"); //$NON-NLS-1$
				shutdown = true;
				break;
			case 'd': //$NON-NLS-1$
				Debug.on();
				clearCallsOnBox();
				shutdown = true;
				break;
			case 'c': //$NON-NLS-1$
				Debug.msg("Clearing Call List"); //$NON-NLS-1$
				JFritz.getCallerList().clearList();
				shutdown = true;
				break;
            case 'i': //$NON-NLS-1$
            	String language = option.getParameter();
            	if(language == null){
            		System.err.println(Main.getMessage("invalid_language")); //$NON-NLS-1$
            		System.err.println("Deutsch: de"); //$NON-NLS-1$
            		System.err.println("English: en"); //$NON-NLS-1$
            		System.exit(0);
            	}else if(language.equals("english") || language.equals("en")){ //$NON-NLS-1$
            		Main.setProperty("locale", "en_US");
            	}else if(language.equals("german") || language.equals("de")){ //$NON-NLS-1$
            		Main.setProperty("locale", "de_DE");
            	}else{
            		System.err.println(Main.getMessage("invalid_language")); //$NON-NLS-1$
            		System.err.println("Deutsch: de"); //$NON-NLS-1$
            		System.err.println("English: en"); //$NON-NLS-1$
            		System.exit(0);
            	}
        		loadMessages(new Locale(Main.getProperty("locale","en_US"))); //$NON-NLS-1$,  //$NON-NLS-2$            		
            	break;
			case 'w': //$NON-NLS-1$
				enableInstanceControl = false;
				System.err.println("Turning off Multiple instance control!"); //$NON-NLS-1$
				System.err.println("You were warned! Data loss may occur."); //$NON-NLS-1$
				break;
			case 'p': //$NON-NLS-1$
				String priority = option.getParameter();
				if (priority == null || priority.equals("")) { //$NON-NLS-1$
					System.err.println(getMessage("parameter_not_found")); //$NON-NLS-1$
					exit(0);
				} else {
					try {
						int level = Integer.parseInt(priority);
						Thread.currentThread().setPriority(level);
						Debug.msg("Set priority to level " + priority); //$NON-NLS-1$
					} catch (NumberFormatException nfe) {
						System.err
								.println(getMessage("parameter_wrong_priority")); //$NON-NLS-1$
						exit(0);
					} catch (IllegalArgumentException iae) {
						System.err
								.println(getMessage("parameter_wrong_priority")); //$NON-NLS-1$
						exit(0);
					}
					break;
				}
			default:
				break;
			}
		}

		if (shutdown) {
			exit(0);
		}
	}

	/**
	 * Ist die Mehrfachstart-�berpr�fung aktiv, so wird ein Dialog angezeigt mit
	 * dem der User JFritz sicher beenden kann
	 * 
	 */
	private void checkInstanceControl() {
		if (enableInstanceControl) {
			// check isRunning and exit or set lock
			File f = new File(SAVE_DIR + LOCK_FILE);
			boolean isRunning = f.exists();

			if (!isRunning) {
				Debug.msg("Multiple instance lock: set lock."); //$NON-NLS-1$
				try {
					f.createNewFile();
				} catch (IOException e) {
					Debug.err("Could not set instance lock");
				}
			} else {
				Debug
						.msg("Multiple instance lock: Another instance is already running."); //$NON-NLS-1$
				int answer = JOptionPane.showConfirmDialog(null,
						getMessage("lock_error_dialog1") //$NON-NLS-1$
								+ getMessage("lock_error_dialog2") //$NON-NLS-1$
								+ getMessage("lock_error_dialog3") //$NON-NLS-1$
								+ getMessage("lock_error_dialog4"), //$NON-NLS-1$
						getMessage("information"), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$
				if (answer == JOptionPane.YES_OPTION) {
					Debug
							.msg("Multiple instance lock: User decided to shut down this instance."); //$NON-NLS-1$
					exit(0);
				} else {
					Debug
							.msg("Multiple instance lock: User decided NOT to shut down this instance."); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * This function writes a file $HOME/.jfritz/jfritz.txt, which contains the
	 * location of the folder containing jfritz's data If the dir $HOME/.jfritz
	 * does not exist, it is created if the save location isnt a directory, then
	 * the default save directory is used
	 * 
	 * @author Brian Jensen
	 * 
	 */
	public static void writeSaveDir() {
		try {

			// if $HOME/.jfritz doesn't exist create it
			File file = new File(USER_DIR);
			if (!file.isDirectory() && !file.isFile())
				file.mkdir();

			BufferedWriter bw = new BufferedWriter(new FileWriter(USER_DIR
					+ File.separator + USER_JFRITZ_FILE, false));

			// make sure the user didn't screw something up
			if (!SAVE_DIR.endsWith(File.separator))
				SAVE_DIR = SAVE_DIR + File.separator;

			file = new File(SAVE_DIR);
			if (!file.isDirectory())
				SAVE_DIR = System.getProperty("user.dir") + File.separator;

			bw.write(SAVE_DIR_TEXT + SAVE_DIR);
			bw.newLine();
			bw.close();
			Debug.msg("Successfully wrote save dir to disk");

		} catch (Exception e) {
			Debug
					.err("Error writing save dir to disk, reverting back to default save dir");
			SAVE_DIR = System.getProperty("user.dir") + File.separator;
			// if there was an error, bail out and revert to the default save
			// location
		}
	}

	/**
	 * Funktion reads the user specified save location from a simple text file
	 * If any error occurs the function bails out and uses the current directory
	 * as the save dir, as the functionality was in JFritz < 0.6.0
	 * 
	 * @author Brian Jensen
	 * 
	 */
	public static void loadSaveDir() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(USER_DIR + File.separator
					+ USER_JFRITZ_FILE));
			String line = br.readLine();
			if (line == null) {
				br.close();
				Debug.msg("File" + USER_DIR + File.separator + USER_JFRITZ_FILE
						+ "empty");
			}
			String[] entries = line.split("=");
			if (!entries[1].equals("")) {
				SAVE_DIR = entries[1];
				File file = new File(SAVE_DIR);
				if (!file.isDirectory())
					SAVE_DIR = System.getProperty("user.dir") + File.separator;
				else if (!SAVE_DIR.endsWith(File.separator))
					SAVE_DIR = SAVE_DIR + File.separator;
			}
			Debug.msg("Save directory: " + SAVE_DIR);
		} catch (FileNotFoundException e) {
			Debug
					.msg("Error processing the user save location(File not found), using defaults");
			// If something happens, just bail out and use the standard dir
		} catch (IOException ioe) {
			Debug
					.msg("Error processing the user save location, using defaults");
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ioe) {
				Debug.msg("Error closing stream");
			}
		}
	}

	protected static void doBackup() {
		CopyFile backup = new CopyFile();
		backup.copy(".", "xml"); //$NON-NLS-1$,  //$NON-NLS-2$
	}

	/**
	 * 
	 */
	public static void clearCallsOnBox() {
		Debug.msg("Clearing callerlist on box."); //$NON-NLS-1$
		JFritzProperties properties = new JFritzProperties();
		try {
			properties.loadFromXML(PROPERTIES_FILE);
		} catch (FileNotFoundException e) {
			Debug.err("File " + PROPERTIES_FILE //$NON-NLS-1$
					+ " not found, using default values"); //$NON-NLS-1$
		} catch (Exception e) {
			Debug.err("Exception: " + e.toString()); //$NON-NLS-1$
		}
		try {
			FritzBox fritzBox = new FritzBox(getProperty(
					"box.address", "192.168.178.1"), Encryption //$NON-NLS-1$,  //$NON-NLS-2$
					.decrypt(getProperty("box.password", Encryption //$NON-NLS-1$
							.encrypt(""))), getProperty("box.port", "80")); //$NON-NLS-1$

			fritzBox.clearListOnFritzBox();
			Debug.msg("Clearing done"); //$NON-NLS-1$
		} catch (WrongPasswordException e) {
			Debug.err("Wrong password, can not delete callerlist on Box."); //$NON-NLS-1$
		} catch (IOException e) {
			Debug
					.err("IOException while deleting callerlist on box (wrong IP-address?)."); //$NON-NLS-1$
		}
	}

	public void exit(int i) {
		/*
		 * isRunning = new Boolean(false); synchronized(mutex){ mutex.notify(); }
		 * 
		 */
		// notifyAll();
		// TODO maybe some cleanup is needed
		Debug.msg("Main.exit(" + i + ")");
		System.exit(i);
	}

	/**
	 * Loads properties from xml files
	 */
	public static void loadProperties() {
		defaultProperties = new JFritzProperties();
		properties = new JFritzProperties(defaultProperties);

		// Default properties
		defaultProperties.setProperty("box.address", "192.168.178.1");//$NON-NLS-1$, //$NON-NLS-2$
		defaultProperties.setProperty("box.password", Encryption.encrypt(""));//$NON-NLS-1$, //$NON-NLS-2$
		defaultProperties.setProperty("box.port", "80");//$NON-NLS-1$, //$NON-NLS-2$
		defaultProperties.setProperty("country.prefix", "00");//$NON-NLS-1$, //$NON-NLS-2$
		defaultProperties.setProperty("area.prefix", "0");//$NON-NLS-1$, //$NON-NLS-2$
		defaultProperties.setProperty("country.code", "+49");//$NON-NLS-1$, //$NON-NLS-2$
		defaultProperties.setProperty("area.code", "441");//$NON-NLS-1$, //$NON-NLS-2$
		defaultProperties.setProperty("fetch.timer", "5");//$NON-NLS-1$, //$NON-NLS-2$

		try {
			properties.loadFromXML(Main.SAVE_DIR + PROPERTIES_FILE);
			replaceOldProperties();
		} catch (FileNotFoundException e) {
			Debug.err("File " + Main.SAVE_DIR + PROPERTIES_FILE //$NON-NLS-1$
					+ " not found => showing config wizard"); //$NON-NLS-1$
			showConfWizard = true;
		} catch (IOException ioe) {
			Debug.err("File " + Main.SAVE_DIR + PROPERTIES_FILE //$NON-NLS-1$
					+ " not readable => showing config wizard"); //$NON-NLS-1$
			showConfWizard = true;
		}
	}

	/**
	 * Replace old property values with new one p.e. column0.width =>
	 * column.type.width
	 * 
	 */
	private static void replaceOldProperties() {
		for (int i = 0; i < 10; i++) {
			removeProperty("SIP" + i); //$NON-NLS-1$
		}

		if (properties.containsKey("column.Typ.width")) { //$NON-NLS-1$
			properties.setProperty("column.type.width", //$NON-NLS-1$
					properties.getProperty("column.Typ.width")); //$NON-NLS-1$
			removeProperty("column.Typ.width"); //$NON-NLS-1$
		}
		if (properties.containsKey("column.Zeitpunkt.width")) { //$NON-NLS-1$
			properties.setProperty("column.date.width", //$NON-NLS-1$
					properties.getProperty("column.Zeitpunkt.width")); //$NON-NLS-1$
			removeProperty("column.Zeitpunkt.width"); //$NON-NLS-1$
		}
		if (properties.containsKey("column.Call-By-Call.width")) { //$NON-NLS-1$
			properties.setProperty("column.callbycall.width", //$NON-NLS-1$
					properties.getProperty("column.Call-By-Call.width")); //$NON-NLS-1$
			removeProperty("column.Call-By-Call.width"); //$NON-NLS-1$
		}
		if (properties.containsKey("column.Rufnummer.width")) { //$NON-NLS-1$
			properties.setProperty("column.number.width", //$NON-NLS-1$
					properties.getProperty("column.Rufnummer.width")); //$NON-NLS-1$
			removeProperty("column.Rufnummer.width"); //$NON-NLS-1$
		}
		if (properties.containsKey("column.Teilnehmer.width")) { //$NON-NLS-1$
			properties.setProperty("column.participant.width", //$NON-NLS-1$
					properties.getProperty("column.Teilnehmer.width")); //$NON-NLS-1$
			removeProperty("column.Teilnehmer.width"); //$NON-NLS-1$
		}
		if (properties.containsKey("column.Anschlu�.width")) { //$NON-NLS-1$
			properties.setProperty("column.port.width", //$NON-NLS-1$
					properties.getProperty("column.Anschlu�.width")); //$NON-NLS-1$
			removeProperty("column.Anschlu�.width"); //$NON-NLS-1$
		}
		if (properties.containsKey("column.MSN.width")) { //$NON-NLS-1$
			properties.setProperty("column.route.width", //$NON-NLS-1$
					properties.getProperty("column.MSN.width")); //$NON-NLS-1$
			removeProperty("column.MSN.width"); //$NON-NLS-1$
		}
		if (properties.containsKey("column.Dauer.width")) { //$NON-NLS-1$
			properties.setProperty("column.duration.width", //$NON-NLS-1$
					properties.getProperty("column.Dauer.width")); //$NON-NLS-1$
			removeProperty("column.Dauer.width"); //$NON-NLS-1$
		}
		if (properties.containsKey("column.Kommentar.width")) { //$NON-NLS-1$
			properties.setProperty("column.comment.width", //$NON-NLS-1$
					properties.getProperty("column.Kommentar.width")); //$NON-NLS-1$
			removeProperty("column.Kommentar.width"); //$NON-NLS-1$
		}
	}

	/**
	 * Saves properties to xml files
	 */
	public static void saveProperties() {
		try {
			Debug.msg("Save other properties"); //$NON-NLS-1$
			properties.storeToXML(Main.SAVE_DIR + PROPERTIES_FILE);
		} catch (IOException e) {
			Debug.err("Couldn't save Properties"); //$NON-NLS-1$
		}
	}

	/**
	 * 
	 * @param property
	 *            Property to get the value from
	 * @param defaultValue
	 *            Default value to be returned if property does not exist
	 * @return Returns value of a specific property
	 */
	public static String getProperty(String property, String defaultValue) {
		return properties.getProperty(property, defaultValue);
	}

	/**
	 * 
	 * @param property
	 *            Property to get the value from
	 * @return Returns value of a specific property
	 */
	public static String getProperty(String property) {
		return getProperty(property, ""); //$NON-NLS-1$
	}

	/**
	 * Sets a property to a specific value
	 * 
	 * @param property
	 *            Property to be set
	 * @param value
	 *            Value of property
	 */
	public static void setProperty(String property, String value) {
		properties.setProperty(property, value);
	}

	/**
	 * Sets a property to a specific value
	 * 
	 * @param property
	 *            Property to be set
	 * @param value
	 *            Value of property
	 */
	public static void setProperty(String property, boolean value) {
		properties.setProperty(property, String.valueOf(value));
	}

	/**
	 * Removes a property
	 * 
	 * @param property
	 *            Property to be removed
	 */
	public static void removeProperty(String property) {
		properties.remove(property);
	}

	/**
	 * Loads resource messages
	 * 
	 * @param locale
	 */
	public static void loadMessages(Locale locale) {
		try {
			messages = ResourceBundle.getBundle("jfritz", locale);//$NON-NLS-1$
		} catch (MissingResourceException e) {
			Debug
					.err("Can't find i18n resource! (\"jfritz_" + locale + ".properties\")");//$NON-NLS-1$
			JOptionPane.showMessageDialog(null, Main.PROGRAM_NAME + " v"//$NON-NLS-1$
					+ Main.PROGRAM_VERSION
					+ "\n\nCannot find the language file \"jfritz_" + locale
					+ ".properties\"!" + "\nProgram will exit!");//$NON-NLS-1$
			System.exit(0);
		}
	}

	/**
	 * Loads locale meanings
	 * 
	 * @param locale
	 */
	private static void loadLocaleMeanings(Locale locale) {
		try {
			localeMeanings = ResourceBundle.getBundle("languages", locale);//$NON-NLS-1$
		} catch (MissingResourceException e) {
			Debug.err("Can't find locale Meanings resource!");//$NON-NLS-1$
		}
	}

	/**
	 * @return Returns an internationalized message. Last modified: 26.04.06 by
	 *         Bastian
	 */
	public static String getMessage(String msg) {
		String i18n = ""; //$NON-NLS-1$
		try {
			if (!messages.getString(msg).equals("")) {
				i18n = messages.getString(msg);
			} else {
				i18n = msg;
			}
		} catch (MissingResourceException e) {
			Debug.err("Can't find resource string for " + msg); //$NON-NLS-1$
			i18n = msg;
		}
		return i18n;
	}

	/**
	 * @return Returns the meanings of a locale abbreviation.
	 */
	public static String getLocaleMeaning(String msg) {
		String localeMeaning = ""; //$NON-NLS-1$
		try {
			if (!localeMeanings.getString(msg).equals("")) {
				localeMeaning = localeMeanings.getString(msg);
			} else {
				localeMeaning = msg;
			}
		} catch (MissingResourceException e) {
			Debug.err("Can't find resource string for " + msg); //$NON-NLS-1$
			localeMeaning = msg;
		} catch (NullPointerException e) {
			Debug.err("Can't find locale Meanings file"); //$NON-NLS-1$
			localeMeaning = msg;
		}
		return localeMeaning;
	}

	public static boolean isInstanceControlEnabled() {
		return enableInstanceControl;
	}

	/**
	 * Checks for systray availability
	 */
	public static boolean checkForSystraySupport() {
		if (!checkSystray)
			return false;
		String os = System.getProperty("os.name"); //$NON-NLS-1$
		if (os.equals("Linux") || os.equals("Solaris") //$NON-NLS-1$,  //$NON-NLS-2$
				|| os.startsWith("Windows")) { //$NON-NLS-1$
			SYSTRAY_SUPPORT = true;
		}
		return SYSTRAY_SUPPORT;
	}

	public static String getHomeDirectory() {
		return jfritzHomedir;
	}

	public JFritz getJfritz() {
		return jfritz;
	}
	
	/**
	 * Speichert die Einstellungen f�r das automatische Update von JFritz
	 *
	 */
	public static void saveUpdateProperties() {
		JFritzUpdate jfritzUpdate = new JFritzUpdate(false);
		Update update = new Update(jfritzUpdate.getPropertiesDirectory());
		update.loadSettings();
		update.setProgramVersion(PROGRAM_VERSION);
		update.setLocale(getProperty("locale", "en_US"));
		update.setUpdateOnStart(JFritzUtils.parseBoolean(Main.getProperty(
				"option.checkNewVersionAfterStart", "false")));
		update.saveSettings();
	}

}