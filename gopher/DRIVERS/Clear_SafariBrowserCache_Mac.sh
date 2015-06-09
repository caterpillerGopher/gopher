#!/bin/bash

# Clear Safari History (except for bookmarks).
/bin/rm -f /Users/$1/Library/Safari/Downloads.plist
/bin/rm -f /Users/$1/Library/Safari/History.plist 
/bin/rm -f /Users/$1/Library/Safari/HistoryIndex.sk
/bin/rm -f  /Users/$1/Library/Safari/TopSites.plist 
/bin/rm -f /Users/$1/Library/Safari/WebpageIcons.db
/bin/rm -f /Users/$1/Library/Safari/LastSession.plist
/bin/rm -rf /Users/$1/Library/Caches/com.apple.Safari
/bin/rm -rf /Users/$1/Library/Cookies/Cookies.binarycookies