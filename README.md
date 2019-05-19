# QClaim

Simple 2D area claiming for Bukkit

## Warning

This was built as a hobby. It's not really intended for servers with hundreds or even tens of people logging on; it was developed rather to keep your friends (read: enemies) from drawing colorful pictures (read: butts) on your majestic skyscrapers (read: dirt shacks). I do plan on working on the plugin more to allow for this, though!

## Install

You can download a .jar from the Releases section. Place it in your plugins folder.

## Use

To create a claim, you'll first need to plot out the two corners of your proposed claim. Walk to each end and type `/qmark`. Then type `/qmark claim` to protect the land you've just marked.

A claim will explicitly stop non-buddies from placing new blocks, destroying blocks, interacting with blocks, igniting blocks, fertilizing blocks, changing signs, using beds, and dumping water/lava inside the claim. Claims reach from bedrock to the top of the sky. 3D support is not planned.

Each claim costs a number of points equal to its area. By default, players receive 22500 points (enough to create a claim 150 blocks wide and 150 blocks tall). Players can view their points at any time by using `/qpoints`. Change this or disable the feature entirely in the config.yml file.

Add buddies who can modify your claims by using `/qbuddy [username]` when they are online. Remove former buddies who have abused this awesome privilege by typing `/qbuddy remove [username]` when they are online.

You can view who owns a claim by typing `/qclaim`. Delete the claim if it's your own (or another's, if you have the proper permission) by typing `/qclaim delete`.

## Permissions

Permissions are set to some sane defaults if you don't want to go overboard with a permission manager.

```
  qclaim.*:
    description: All commands and free claims
    default: op
  qclaim.view:
    description: View who owns claims
    default: true
  qclaim.claim.*:
    description: Define, create, and remove claims
    children:
      qclaim.claim.mark:
        description: Mark claims
        default: true
      qclaim.claim.claim:
        description: Finalize and create claims
        default: true
      qclaim.claim.remove:
        description: Remove (own) claims
        default: true
  qclaim.admin.*:
    description: Grants access to free claims and admin commands.
    default: op
    children:
      qclaim.admin.free:
        description: With this permission, all claims are free
        default: op
      qclaim.admin.remove:
        description: Remove (other's) claims
        default: op
      qclaim.admin.ignore:
        description: Allow this user to build in claims that are not their own
        default: op
  qclaim.buddy:
    description: Add/remove buddies (people who can build in one's own claims)
    default: true
```

## Building this yourself

It's a Maven project. You can just run `mvn package` on it.

## License

```
QClaim - Simple 2D area claiming for Bukkit
Copyright (C) 2019 Cutie Cafe

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```