#!/bin/bash
#set -x
input_file="model.hmr"
output_file="model2.hmr"
#input_file="model2.hmr.hmr"
#output_file="model.hmr.hmr"

###### INPUT BEGIN ######
declare -a input_array=(
"R1"
"R2"
"agh.heart.callbacks.Location"
"agh.heart.callbacks.Gyroscope"
"agh.heart.callbacks.Screen"
"agh.heart.actions.HowAreYou_StartQuestionColor"
"agh.heart.actions.HowAreYou_StartQuestionEmoji"
)

declare -a output_array=(
",R,1,"
",R,2,"
"L1"
"L2"
"L3"
"R1"
"R2"
)
###### INPUT END ######




cp $input_file $output_file

do_reverse_replacement=1
for ((i=0;i<${#input_array[@]};++i)); do
    input_string="${input_array[i]}"
    if grep -q "$input_string" "$input_file"; then
        do_reverse_replacement=0
        break
    fi
done


if (( $do_reverse_replacement == 1 )); then
    echo "Reverse replacement."
else
    echo "Direct replacement."
fi

for ((i=0;i<${#input_array[@]};++i)); do
    if (( $do_reverse_replacement == 1 )); then
        output_string="${input_array[i]}"
        input_string="${output_array[i]}"
    else
        input_string="${input_array[i]}"
        output_string="${output_array[i]}"
    fi
    sed "s/$input_string/$output_string/g" $output_file | tee $output_file > /dev/null
done
#set +x