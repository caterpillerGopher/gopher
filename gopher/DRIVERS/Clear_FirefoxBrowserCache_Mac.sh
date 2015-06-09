#!/bin/bash
# A script to clear out Firefox data from the command line.
# MIT License <http://opensource.org/licenses/MIT>

src="/Users/$1/Library/Application Support/Firefox/Profiles"

declare -a files_to_delete=(places.sqlite places.sqlite-shm places.sqlite-wal downloads.sqlite formhistory.sqlite search-metadata.json search.json search.sqlite cookies.sqlite cookies.sqlite-shm cookies.sqlite-wal signons.sqlite sessionstore.bak sessionstore.js)

declare -a directories_to_delete=(Cache OfflineCache)

# enable for loops over items with spaces
IFS=$'\n'

# loop through browser profiles and delete certain files
for dir in `ls "$src/"`
do
    if [ -d "$src/$dir" ]; then
        for item in "${files_to_delete[@]}"
        do
            if [ -f "$src/$dir/$item" ]; then
                rm "$src/$dir/$item"
                echo "deleting: $src/$dir/$item"
            fi
        done
        for item in "${directories_to_delete[@]}"
        do
            if [ -d "$src/$dir/$item" ]; then
                rm -r "$src/$dir/$item"
                echo "deleting: $src/$dir/$item"
            fi
        done
    fi
done 
