
Pod::Spec.new do |s|
  s.name         = "RNEasyIjkplayer"
  s.version      = "1.0.0"
  s.summary      = "RNEasyIjkplayer"
  s.description  = <<-DESC
                  IJKPlayer for React Native
                   DESC
  s.homepage     = ""
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/author/RNEasyIjkplayer.git", :tag => "master" }
  s.source_files  = "ios/**/*.{h,m}"
  s.homepage = "https://github.com/ming436534/react-native-easy-ijkplayer.git"
  s.requires_arc = true


  s.dependency "React"
  s.dependency 'ijkplayer'
  #s.dependency "others"

end

  