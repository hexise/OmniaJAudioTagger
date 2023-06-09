# JAudioTagger
A modified version of JAudioTagger library for Omnia Music Player.

## Changes
The main goal for this library is to support document API since Android 11 (API Level 30).

Original JAudioTagger library only can parse java.io.File, this library can parse both java.io.File and androidx.documentfile.provider.DocumentFile, just implement the interface IFileEntry.

This library has a set of new modified JAudioTagger classes to read audio files (parsing tags) from DocumentFile. Writing to DocumentFile (saving tag changes) is not supported. You can request one time file writing permission on Android side, then use Java IO to save tag modifications.

This library only open source the modified version of JAudioTagger, not player related implementations.

## License
This library is licensed under LGPL (Lesser General Public License), same as JAudioTagger official library.