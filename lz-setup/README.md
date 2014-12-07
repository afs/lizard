Material to help setup development systems for Lizard.

* Don't forget to set user/email in your `.git/config` if `${HOME}/.gitconfig` is different 
* A `.gitignore`
* `org.eclipse.jdt.ui.prefs` : set the template to create the right Java headers (copyright/license) for new files.

```
cd $LIZARD_HOME
for m in lizard-* lz-*
do 
   cp $LIZARD_HOME/lz-setup/org.eclipse.jdt.ui.prefs $m/.settings
done
```
