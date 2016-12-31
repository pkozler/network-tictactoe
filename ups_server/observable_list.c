/* 
 * Author: Petr Kozler
 */

#include "observable_list.h"
#include "config.h"
#include <stdlib.h>

bool is_id_valid(int32_t id) {
    return id > 0;
}

bool is_name_valid(char *name) {
    if (name == NULL) {
        return false;
    }
    
    int32_t len = strlen(name);
    
    if (len < 1 || MAX_NAME_LENGTH < len) {
        return false;
    }
    
    int32_t i;
    for (i = 0; name[i] != '\0'; i++) {
        if (   !(name[i] >= 'a' || name[i] <= 'z')
            && !(name[i] >= 'A' || name[i] <= 'Z')
            && !(name[i] >= '0' || name[i] <= '9')
            && !(name[i] == '-' || name[i] == '_')) {
            return false;
        }
    }
    
    return true;
}
