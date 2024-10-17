#!/bin/sh
exec mvn --errors -Dbnd.baseline.skip=true -Denforcer.skip=true clean verify
