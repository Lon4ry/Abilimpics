rm -rf "out"
mkdir -p "out"
for dir in services/*/
do
  cd $dir || exit;
  ./gradlew clean build -x test -Dorg.gradle.java.home=/Users/lon4ry/Library/Java/JavaVirtualMachines/liberica-full-17.0.10
  cd ../../;

  out_file=$(echo $dir | cut -d '/' -f 2)
  tag=abilimpus-$(echo $out_file | tr '[:upper:]' '[:lower:]')
  docker build --target local-runtime --tag $tag --output type=oci,dest="out/$out_file" \
    --build-arg \
      DIR="$dir" \
    .

  docker load -i "./out/$out_file"
done

docker compose -f compose.yml up -d